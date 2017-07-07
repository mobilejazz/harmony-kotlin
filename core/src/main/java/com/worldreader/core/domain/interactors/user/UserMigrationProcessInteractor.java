package com.worldreader.core.domain.interactors.user;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.application.di.qualifiers.WorldreaderNetworkCacheDb;
import com.worldreader.core.application.di.qualifiers.WorldreaderUserApiEndpoint;
import com.worldreader.core.application.di.qualifiers.WorldreaderUserServer;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.datasource.deprecated.mapper.Mapper;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.datasource.model.user.LevelEntity;
import com.worldreader.core.datasource.model.user.UserReadingStatsEntity;
import com.worldreader.core.datasource.model.user.milestones.MilestoneEntity;
import com.worldreader.core.datasource.network.model.LeaderboardStatNetwork;
import com.worldreader.core.datasource.storage.datasource.cache.CacheBddDataSource;
import com.worldreader.core.domain.model.user.User2;
import java.io.File;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@PerActivity public class UserMigrationProcessInteractor {

  private static final String OLD_DB_NAME = "app.worldreader.cache.db";

  private final ListeningExecutorService executor;

  private final HttpUrl httpUrl;
  private final OkHttpClient okHttpClient;
  private final CacheBddDataSource dataSource;
  private final Reachability reachability;
  private final Gson gson;

  private final GetUserInteractor getUserInteractor;
  private final SaveUserInteractor saveUserInteractor;
  private final AfterLogInUserProcessInteractor afterLogInUserProcessInteractor;

  @Inject public UserMigrationProcessInteractor(@WorldreaderUserApiEndpoint final HttpUrl httpUrl,
      @WorldreaderNetworkCacheDb final CacheBddDataSource dataSource, @WorldreaderUserServer OkHttpClient okHttpClient,
      final ListeningExecutorService executor, final Reachability reachability, final Gson gson, final GetUserInteractor getUserInteractor,
      final SaveUserInteractor saveUserInteractor, final AfterLogInUserProcessInteractor afterLogInUserProcessInteractor) {
    this.httpUrl = httpUrl;
    this.dataSource = dataSource;
    this.okHttpClient = okHttpClient;
    this.executor = executor;
    this.reachability = reachability;
    this.gson = gson;
    this.getUserInteractor = getUserInteractor;
    this.saveUserInteractor = saveUserInteractor;
    this.afterLogInUserProcessInteractor = afterLogInUserProcessInteractor;
  }

  public ListenableFuture<Boolean> execute(final Context context) {
    return executor.submit(getInteractorCallable(context));
  }

  private Callable<Boolean> getInteractorCallable(final Context context) {
    return new Callable<Boolean>() {
      @Override public Boolean call() throws Exception {
        // Try to load the DB
        final File oldDBFile = context.getDatabasePath(OLD_DB_NAME);

        if (oldDBFile.exists()) {
          // Try to open the dB
          final SQLiteDatabase db = SQLiteDatabase.openDatabase(oldDBFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

          // Extract the user in there
          final Cursor cursor = db.rawQuery("SELECT value FROM cache WHERE _key LIKE 'user.key'", new String[] {});

          // Extract column
          final boolean existUser = cursor.getCount() > 0;

          if (!existUser) {
            // Close everything before proceeding
            cursor.close();
            db.close();

            // Clean this DB
            oldDBFile.delete();

            // We can return safely
            return true;
          }

          // Move to first result and only result
          cursor.moveToFirst();

          // Go to value column
          final byte[] rawBlob = cursor.getBlob(0);

          // Close everything before proceeding
          cursor.close();
          db.close();

          // Convert it to UserEntity
          final String userJson = new String(rawBlob, "UTF-8");
          final UserEntity user = gson.fromJson(userJson, new TypeToken<UserEntity>() {
          }.getType());

          // Check if user is anonymous
          if (user.isRegister) {
            // Convert user to JSON

            final UserNetworkDataMapper mapper = new UserNetworkDataMapper();
            final UserNetwork userNetwork = mapper.transformInverse(user);

            final String userNetworkJson = gson.toJson(userNetwork);

            final RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), userNetworkJson);
            final Request request = new Request.Builder().url(httpUrl.newBuilder().addPathSegment("me").build()).post(body).build();

            // Send user to server
            final Response response = okHttpClient.newCall(request).execute();
            final boolean successful = response.isSuccessful();

            if (successful) {
              // Fetch new user from repository
              final User2 user2 = getUserInteractor.execute(MoreExecutors.directExecutor()).get();

              // Store user in DB
              final SaveUserInteractor.Type type = SaveUserInteractor.Type.LOGGED_IN;
              saveUserInteractor.execute(user2, type, MoreExecutors.directExecutor());

              // Kickstart synchronization process
              afterLogInUserProcessInteractor.execute(user2, MoreExecutors.directExecutor());

              // Clean this DB
              //oldDBFile.delete();

              // Everything went OK
              return true;
            } else {
              // Problem updating user into backend (maybe a failure in Internet?) Don't delete the database and notify back user
              return false;
            }
          } else {
            // If user is anonymous, we have to recreate an anonymous user an all of it's information
            final OldUserToNewUserMapper mapper = new OldUserToNewUserMapper();

            // Transform old user to new one
            final User2 user2 = mapper.transform(user);

            // Generate UserBooks

            // Store user in DB
            final SaveUserInteractor.Type type = SaveUserInteractor.Type.ANONYMOUS;
            saveUserInteractor.execute(user2, type, MoreExecutors.directExecutor());
          }
        } else {
          return true;
        }

        return false;
      }
    };
  }

  private static class UserEntity {

    public static final String ANONYMOUS_NAME = "Anonymous";

    public int id;
    public String name;
    public String email;
    public String password;
    public String birthDate;
    public String cloudinaryPhotoId;
    public boolean isRegister;
    public List<Integer> categories;
    public List<String> favoritesBooks;
    public List<String> booksCurrentlyReading;
    public String username;
    public Map<Integer, List<String>> openedBooksMap;
    public Map<Integer, List<String>> finishedBooks;
    public int expectedNumberOfPagesPerDayRead;
    public boolean hasChildren;
    public int minChildrenAge;
    public int maxChildrenAge;
    public Set<LevelEntity> levels;
    public int userScore;

    public UserEntity() {
      levels = new LinkedHashSet<>();
    }

  }

  private static class UserNetwork {

    @SerializedName("id") private int id;
    @SerializedName("email") private String email;
    @SerializedName("username") private String username;
    @SerializedName("name") private String name;
    @SerializedName("last_name") private String lastName;
    @SerializedName("birth_date") private long birthdate;
    @SerializedName("role") private int role;
    @SerializedName("picture") private String picture;
    @SerializedName("created_at") private long createdAt;
    @SerializedName("categories") private List<CategoryNetwork> categories;
    @SerializedName("favorites") private List<BookNetwork> favoriteBooks;
    @SerializedName("reading") private List<BookNetwork> currentlyReadingBooks;
    @SerializedName("opened_map") private List<BookOpenedNetwork> openedBooks;
    @SerializedName("read_map") private List<FinishedBookNetwork> finishedBooks;
    @SerializedName("user_profile") private UserProfileNetwork userProfileNetwork;
    @SerializedName("milestone") private List<Integer> milestones;

    public UserNetwork() {
    }

    public UserNetwork(int id, String email, String username, String name, String lastName, long birthdate, int role, String picture, long createdAt,
        List<CategoryNetwork> categories, List<BookNetwork> favoriteBooks, List<BookNetwork> currentlyReadingBooks,
        List<BookOpenedNetwork> openedBooks, UserProfileNetwork userProfileNetwork, List<FinishedBookNetwork> finishedBookNetwork,
        List<Integer> milestones) {
      this.id = id;
      this.email = email;
      this.username = username;
      this.name = name;
      this.lastName = lastName;
      this.birthdate = birthdate;
      this.role = role;
      this.picture = picture;
      this.createdAt = createdAt;
      this.categories = categories;
      this.favoriteBooks = favoriteBooks;
      this.currentlyReadingBooks = currentlyReadingBooks;
      this.openedBooks = openedBooks;
      this.userProfileNetwork = userProfileNetwork;
      this.finishedBooks = finishedBookNetwork;
      this.milestones = milestones;
    }
  }

  private static class CategoryNetwork {

    @SerializedName("user_id") public int userId;
    @SerializedName("category_id") public int categoryId;

  }

  private static class BookNetwork {

    @SerializedName("user_id") public int userId;
    @SerializedName("book") public String bookId;
    @SerializedName("order") public int order;

    private BookNetwork(int userId, String bookId, int order) {
      this.userId = userId;
      this.bookId = bookId;
      this.order = order;
    }

    public static BookNetwork create(int userId, String bookId, int order) {
      return new BookNetwork(userId, bookId, order);
    }
  }

  private static class BookOpenedNetwork {

    @SerializedName("user_id") public int userId;
    @SerializedName("key") public int collectionId;
    @SerializedName("value") public String bookId;

    public BookOpenedNetwork(int userId, int collectionId, String bookId) {
      this.userId = userId;
      this.collectionId = collectionId;
      this.bookId = bookId;
    }

    public static BookOpenedNetwork create(int userId, int collectionId, String bookId) {
      return new BookOpenedNetwork(userId, collectionId, bookId);
    }

  }

  private static class FinishedBookNetwork {

    @SerializedName("user_id") public int userId;
    @SerializedName("key") public int collectionId;
    @SerializedName("value") public String bookId;

    public FinishedBookNetwork(int userId, int collectionId, String bookId) {
      this.userId = userId;
      this.collectionId = collectionId;
      this.bookId = bookId;
    }

    public static FinishedBookNetwork create(int userId, int collectionId, String bookId) {
      return new FinishedBookNetwork(userId, collectionId, bookId);
    }
  }

  private static class UserProfileNetwork {

    @SerializedName("pages_per_day") public int pagesPerDay;
    @SerializedName("children") public int children;
    @SerializedName("min_children_age") public int minChildrenAge;
    @SerializedName("max_children_age") public int maxChildrenAge;
    @SerializedName("user_score") public int score;

    public UserProfileNetwork() {
    }

    public UserProfileNetwork(int pagesPerDay, int children, int minChildrenAge, int maxChildrenAge, int score) {
      this.pagesPerDay = pagesPerDay;
      this.children = children;
      this.minChildrenAge = minChildrenAge;
      this.maxChildrenAge = maxChildrenAge;
      this.score = score;
    }

  }

  private static class UserReadingStatsNetwork {

    @SerializedName("statistics") List<Stat> stats;

    public List<Stat> getStats() {
      return stats;
    }

    public void setStats(List<Stat> stats) {
      this.stats = stats;
    }

    public static class Stat {

      public Stat(String date, int readCount) {
        this.date = date;
        this.count = readCount;
      }

      @SerializedName("date") String date;
      @SerializedName("value") int count;

      public String getDate() {
        return date;
      }

      public void setDate(String date) {
        this.date = date;
      }

      public int getReadCount() {
        return count;
      }

      public void setReadCount(int readCount) {
        this.count = readCount;
      }
    }
  }

  private static class UserNetworkDataMapper implements Mapper<UserEntity, UserNetwork> {

    public final String TAG = UserNetworkDataMapper.class.getSimpleName();

    public UserNetworkDataMapper() {
    }

    @Override public UserEntity transform(UserNetwork data) {
      return null;
    }

    @Override public List<UserEntity> transform(List<UserNetwork> data) {
      throw new IllegalStateException("transform(List<UserNetwork> data) not supported");
    }

    @Override public UserNetwork transformInverse(UserEntity data) {
      UserNetwork userNetwork = new UserNetwork();
      userNetwork.id = data.id;
      userNetwork.username = data.username;
      userNetwork.name = data.name;
      userNetwork.email = data.email;

      if (data.birthDate == null) {
        userNetwork.birthdate = new Date().getTime();
      } else {
        Date parsed;
        try {
          parsed = ISO8601Utils.parse(data.birthDate, new ParsePosition(0));
        } catch (ParseException e) {
          parsed = new Date();
        }
        userNetwork.birthdate = parsed.getTime() / 1000L; //data.birthDate.getTime() / 1000L;
      }

      userNetwork.picture = data.cloudinaryPhotoId;

      userNetwork.categories = getCategoryIds(data);
      userNetwork.favoriteBooks = getFavoriteBookIds(data);
      userNetwork.currentlyReadingBooks = getBooksCurrentlyReading(data);
      userNetwork.openedBooks = getOpennedBooks(data);
      userNetwork.userProfileNetwork = getUserProfileNetwork(data);
      userNetwork.finishedBooks = getFinishedBooks(data);

      return userNetwork;
    }

    @Override public List<UserNetwork> transformInverse(List<UserEntity> data) {
      throw new IllegalStateException("transform(List<UserEntity> data) not supported");
    }

    private UserProfileNetwork getUserProfileNetwork(UserEntity data) {
      UserProfileNetwork userProfileNetwork = new UserProfileNetwork();

      userProfileNetwork.minChildrenAge = data.minChildrenAge;
      userProfileNetwork.maxChildrenAge = data.maxChildrenAge;
      userProfileNetwork.children = (data.hasChildren ? 1 : 0);
      userProfileNetwork.pagesPerDay = data.expectedNumberOfPagesPerDayRead;
      userProfileNetwork.score = data.userScore;

      return userProfileNetwork;
    }

    public UserReadingStatsEntity transform(UserReadingStatsNetwork userReadingStatsNetwork) {
      UserReadingStatsEntity userReadingStatsEntity = new UserReadingStatsEntity();

      List<UserReadingStatsEntity.Stat> stats = new ArrayList<>(userReadingStatsNetwork.getStats().size());

      for (UserReadingStatsNetwork.Stat stat : userReadingStatsNetwork.getStats()) {
        stats.add(new UserReadingStatsEntity.Stat(stat.getDate(), stat.getReadCount()));
      }

      //userReadingStatsEntity.setStats(stats);

      return userReadingStatsEntity;
    }

    public LeaderboardStatEntity transform(LeaderboardStatNetwork leaderboardStatNetwork) {
      return new LeaderboardStatEntity(leaderboardStatNetwork.getUsername(), leaderboardStatNetwork.getRank(), leaderboardStatNetwork.getScore());
    }

    //region Private Methods

    private List<CategoryNetwork> getCategoryIds(UserEntity data) {
      List<Integer> categories = data.categories;
      List<CategoryNetwork> categoriesNetwork = new ArrayList<>(categories.size());

      for (Integer categoryId : categories) {
        CategoryNetwork categoryNetwork = new CategoryNetwork();
        categoryNetwork.userId = data.id;
        categoryNetwork.categoryId = categoryId;

        categoriesNetwork.add(categoryNetwork);
      }

      return categoriesNetwork;
    }

    private List<BookNetwork> getFavoriteBookIds(UserEntity data) {
      List<String> favoritesBooksIds = data.favoritesBooks;
      List<BookNetwork> favoriteBooks = new ArrayList<>(favoritesBooksIds.size());

      for (int position = 0; position < favoritesBooksIds.size(); position++) {
        String bookId = favoritesBooksIds.get(position);
        BookNetwork bookNetwork = BookNetwork.create(data.id, bookId, position);
        favoriteBooks.add(bookNetwork);
      }

      return favoriteBooks;
    }

    private List<BookNetwork> getBooksCurrentlyReading(UserEntity data) {
      List<String> bookCurrentlyReadingIds = data.booksCurrentlyReading;
      List<BookNetwork> booksCurrentlyReading = new ArrayList<>(bookCurrentlyReadingIds.size());

      for (int position = 0; position < bookCurrentlyReadingIds.size(); position++) {
        String bookId = bookCurrentlyReadingIds.get(position);
        BookNetwork bookNetwork = BookNetwork.create(data.id, bookId, position);
        booksCurrentlyReading.add(bookNetwork);
      }

      return booksCurrentlyReading;
    }

    private List<BookOpenedNetwork> getOpennedBooks(UserEntity data) {
      List<BookOpenedNetwork> booksOpened = new ArrayList<>();

      Map<Integer, List<String>> openedBooksMap = data.openedBooksMap;
      Set<Integer> collectionIds = openedBooksMap.keySet();
      for (Integer collectionId : collectionIds) {
        List<String> bookIds = openedBooksMap.get(collectionId);
        for (String bookId : bookIds) {
          BookOpenedNetwork bookOpenedNetwork = BookOpenedNetwork.create(data.id, collectionId, bookId);
          booksOpened.add(bookOpenedNetwork);
        }
      }

      return booksOpened;
    }

    private List<FinishedBookNetwork> getFinishedBooks(UserEntity data) {
      List<FinishedBookNetwork> booksOpened = new ArrayList<>();

      Map<Integer, List<String>> finishedBookMap = data.finishedBooks;
      Set<Integer> collectionIds = finishedBookMap.keySet();
      for (Integer collectionId : collectionIds) {
        List<String> bookIds = finishedBookMap.get(collectionId);
        for (String bookId : bookIds) {
          FinishedBookNetwork bookOpenedNetwork = FinishedBookNetwork.create(data.id, collectionId, bookId);
          booksOpened.add(bookOpenedNetwork);
        }
      }

      return booksOpened;
    }
    //endregion
  }

  private static class OldUserToNewUserMapper implements com.worldreader.core.datasource.mapper.Mapper<UserEntity, User2> {

    @Override public User2 transform(final UserEntity raw) {
      // Parse birthdate
      Date birthDate;
      try {
        birthDate = ISO8601Utils.parse(raw.birthDate, new ParsePosition(0));
      } catch (ParseException e) {
        birthDate = new Date();
      }

      // Parse categories
      List<Integer> categories = raw.categories;
      List<String> categoriesConverted = new ArrayList<>();

      if (categories != null && categories.size() > 0) {
        for (final Integer category : categories) {
          categoriesConverted.add(String.valueOf(category));
        }
      }

      // Obtain only completed milestones
      final Set<LevelEntity> levels = raw.levels;
      final List<Integer> milestones = new ArrayList<>();
      if (levels != null) {
        for (final LevelEntity level : levels) {
          final Set<MilestoneEntity> milestonesList = level.getMilestones();
          for (final MilestoneEntity entity : milestonesList) {
            if (entity.getState() == MilestoneEntity.State.DONE) {
              milestones.add(entity.getId());
            }
          }
        }
      }

      final User2 user2 = new User2.Builder().setName(raw.name)
          .setUserName(raw.username)
          .setBirthDate(birthDate)
          .setEmail(raw.email)
          .setLocalLibrary(null)
          //.setAge(raw.age)
          //.setChildrenCount(raw.chi)
          .setCreatedAt(new Date())
          .setFavoriteCategories(categoriesConverted)
          .setMaxChildAge(raw.maxChildrenAge)
          .setMinChildAge(raw.minChildrenAge)
          .setPagesPerDay(raw.expectedNumberOfPagesPerDayRead)
          .setPicture(raw.cloudinaryPhotoId)
          .setMilestones(milestones)
          //.setProfileId()
          //.setReadToKidsId()
          .build();


      return user2;
    }
  }
}
