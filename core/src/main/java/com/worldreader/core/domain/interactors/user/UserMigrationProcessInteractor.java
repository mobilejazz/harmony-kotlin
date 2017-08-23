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
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.application.di.annotation.PerActivity;
import com.worldreader.core.application.di.qualifiers.WorldreaderUserApiEndpoint;
import com.worldreader.core.application.di.qualifiers.WorldreaderUserServer;
import com.worldreader.core.concurrency.SafeCallable;
import com.worldreader.core.datasource.deprecated.mapper.Mapper;
import com.worldreader.core.datasource.model.LeaderboardStatEntity;
import com.worldreader.core.datasource.model.user.LevelEntity;
import com.worldreader.core.datasource.model.user.UserReadingStatsEntity;
import com.worldreader.core.datasource.model.user.milestones.MilestoneEntity;
import com.worldreader.core.datasource.network.model.LeaderboardStatNetwork;
import com.worldreader.core.datasource.spec.milestones.PutUserMilestonesStorageSpec;
import com.worldreader.core.datasource.spec.user.UserStorageSpecification;
import com.worldreader.core.datasource.spec.userbooks.PutAllUserBooksStorageSpec;
import com.worldreader.core.datasource.spec.userbookslike.PutAllUserBookLikeStorageSpec;
import com.worldreader.core.datasource.storage.datasource.cache.manager.table.UsersTable;
import com.worldreader.core.domain.interactors.user.milestones.CreateUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.milestones.PutAllUserMilestonesInteractor;
import com.worldreader.core.domain.interactors.user.score.AddUserScoreInteractor;
import com.worldreader.core.domain.interactors.user.userbooks.PutAllUserBooksInteractor;
import com.worldreader.core.domain.interactors.user.userbookslike.PutAllUserBooksLikesInteractor;
import com.worldreader.core.domain.model.user.User2;
import com.worldreader.core.domain.model.user.UserBook;
import com.worldreader.core.domain.model.user.UserBookLike;
import com.worldreader.core.domain.model.user.UserMilestone;
import com.worldreader.core.domain.model.user.UserScore;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.*;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.*;
import java.util.concurrent.*;

// TODO: 12/07/2017 Review expired token
@PerActivity public class UserMigrationProcessInteractor {

  private static final String TAG = UserMigrationProcessInteractor.class.getSimpleName();

  private static final String OLD_DB_NAME = "app.worldreader.cache.db";

  private final ListeningExecutorService executor;

  private final HttpUrl httpUrl;
  private final OkHttpClient okHttpClient;
  private final Gson gson;

  private final GetUserInteractor getUserInteractor;
  private final SaveUserInteractor saveUserInteractor;
  private final PutAllUserBooksInteractor putAllUserBooksInteractor;

  private final AfterLogInUserProcessInteractor afterLogInUserProcessInteractor;

  private final CreateUserMilestonesInteractor createUserMilestonesInteractor;
  private final PutAllUserMilestonesInteractor putAllUserMilestonesInteractor;
  private final PutAllUserBooksLikesInteractor putAllUserBooksLikesInteractor;
  private final AddUserScoreInteractor addUserScoreInteractor;

  private final Logger logger;

  @Inject public UserMigrationProcessInteractor(@WorldreaderUserApiEndpoint final HttpUrl httpUrl, @WorldreaderUserServer OkHttpClient okHttpClient,
      final ListeningExecutorService executor, final Gson gson, final GetUserInteractor getUserInteractor,
      final SaveUserInteractor saveUserInteractor, final PutAllUserBooksInteractor putAllUserBooksInteractor,
      final AfterLogInUserProcessInteractor afterLogInUserProcessInteractor, final CreateUserMilestonesInteractor createUserMilestonesInteractor,
      final PutAllUserMilestonesInteractor putAllUserMilestonesInteractor, final PutAllUserBooksLikesInteractor putAllUserBooksLikesInteractor,
      final AddUserScoreInteractor addUserScoreInteractor, final Logger logger) {
    this.httpUrl = httpUrl;
    this.okHttpClient = okHttpClient;
    this.executor = executor;
    this.gson = gson;
    this.getUserInteractor = getUserInteractor;
    this.saveUserInteractor = saveUserInteractor;
    this.putAllUserBooksInteractor = putAllUserBooksInteractor;
    this.afterLogInUserProcessInteractor = afterLogInUserProcessInteractor;
    this.createUserMilestonesInteractor = createUserMilestonesInteractor;
    this.putAllUserMilestonesInteractor = putAllUserMilestonesInteractor;
    this.putAllUserBooksLikesInteractor = putAllUserBooksLikesInteractor;
    this.addUserScoreInteractor = addUserScoreInteractor;
    this.logger = logger;
  }

  public ListenableFuture<Boolean> execute(final Context context) {
    return executor.submit(getInteractorCallable(context));
  }

  private Callable<Boolean> getInteractorCallable(final Context context) {
    return new SafeCallable<Boolean>() {

      @Override protected void onExceptionThrown(final Throwable t) {
        logger.e(TAG, "Error migrating the old user.");
        logger.d(TAG, t.getMessage());
      }

      @Override public Boolean safeCall() throws Throwable {
        // Try to load the DB
        logger.d(TAG, "Loading the old database");
        final File oldDBFile = context.getDatabasePath(OLD_DB_NAME);

        if (oldDBFile.exists()) {
          // Try to open the dB
          logger.d(TAG, "Old database found!");
          final SQLiteDatabase db = SQLiteDatabase.openDatabase(oldDBFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);

          // Extract the user in there
          logger.d(TAG, "Trying to extract the old user");
          final Cursor cursor = db.rawQuery("SELECT value FROM cache WHERE _key LIKE 'user.key'", new String[] {});

          // Extract column
          final boolean existUser = cursor.getCount() > 0;

          if (!existUser) {
            logger.d(TAG, "Old user not found. Cleaning old database!");

            // Close everything before proceeding
            cursor.close();
            db.close();

            // Clean this DB
            oldDBFile.delete();

            // We can return safely
            return true;
          }

          // Move to first result and only result
          logger.d(TAG, "Obtaining old user");
          cursor.moveToFirst();

          // Go to value column
          final byte[] rawBlob = cursor.getBlob(0);

          // Close everything before proceeding
          logger.d(TAG, "Closing old database file");
          cursor.close();
          db.close();

          // Convert it to UserEntity
          logger.d(TAG, "Converting raw old user data to user entity");
          final String userJson = new String(rawBlob, "UTF-8");
          final UserEntity user = gson.fromJson(userJson, new TypeToken<UserEntity>() {
          }.getType());

          // Check if user is anonymous
          logger.d(TAG, "Is old user anonymous?");

          if (user.isRegister) {
            logger.d(TAG, "Old user was registered");

            // Convert user to JSON
            logger.d(TAG, "Converting user to old user network model");
            final UserNetworkDataMapper mapper = new UserNetworkDataMapper();
            final UserNetwork userNetwork = mapper.transformInverse(user);

            final String userNetworkJson = gson.toJson(userNetwork);

            logger.d(TAG, "Sending old user network model through network");
            final RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), userNetworkJson);
            final Request request = new Request.Builder().url(httpUrl.newBuilder().addPathSegment("me").build()).post(body).build();

            // Send user to server
            final Response response = okHttpClient.newCall(request).execute();
            final boolean successful = response.isSuccessful();

            if (successful) {
              logger.d(TAG, "Successfully sent old network user model");

              // Fetch new user from repository
              logger.d(TAG, "Fetching new user model from network");
              final User2 user2 = getUserInteractor.execute(MoreExecutors.directExecutor()).get();

              // Store user in DB
              logger.d(TAG, "Store it in storage");
              final SaveUserInteractor.Type type = SaveUserInteractor.Type.LOGGED_IN;
              saveUserInteractor.execute(user2, type, MoreExecutors.directExecutor()).get();

              // Kickstart synchronization process
              logger.d(TAG, "Kick starting after login user process interactor");
              afterLogInUserProcessInteractor.execute(user2, MoreExecutors.directExecutor()).get();

              logger.d(TAG, "Storing UserBooksLikes in storage");
              final List<BookNetwork> favoriteBooks = userNetwork.favoriteBooks;
              final List<UserBookLike> userBookLikes = new ArrayList<>(favoriteBooks.size());
              for (final BookNetwork favoriteBook : favoriteBooks) {
                final UserBookLike userBookLike =
                    new UserBookLike.Builder().withBookId(favoriteBook.bookId)
                        .withLiked(true)
                        .withUserId(String.valueOf(favoriteBook.userId))
                        .withLikedAt(new Date())
                        .withSync(false)
                        .build();

                userBookLikes.add(userBookLike);
              }

              final PutAllUserBookLikeStorageSpec userBookLikeStorageSpec =
                  new PutAllUserBookLikeStorageSpec(UserStorageSpecification.UserTarget.LOGGED_IN);
              putAllUserBooksLikesInteractor.execute(userBookLikes, userBookLikeStorageSpec,
                  MoreExecutors.directExecutor()).get();

              // Clean this DB
              logger.d(TAG, "Cleaning old database file");
              oldDBFile.delete();

              // Everything went OK
              logger.d(TAG, "Everything went OK");
              return true;
            } else {
              // Problem updating user into backend (maybe a failure in Internet?) Don't delete the database and notify back user
              logger.d(TAG, "There was a problem updating old user into backend. Aborting process");
              return false;
            }
          } else {
            // If user is anonymous, we have to recreate an anonymous user an all of it's information
            logger.d(TAG, "Old User is anonymous");
            final OldUserToNewUserMapper mapper = new OldUserToNewUserMapper();

            // Transform old user to new one
            logger.d(TAG, "Starting transformation process of old user into newer one");
            user.id = Integer.valueOf(UsersTable.ANONYMOUS_USER_ID);
            final User2 user2 = mapper.transform(user);

            // Store user in DB
            logger.d(TAG, "Storing new user into db");
            final SaveUserInteractor.Type type = SaveUserInteractor.Type.ANONYMOUS;
            saveUserInteractor.execute(user2, type, MoreExecutors.directExecutor()).get();

            // Generate initial UserBooks collection with read books
            logger.d(TAG, "Generate initial UserBooks collection");
            List<UserBook> userBooks = createReadUserBooks(user);

            // Update UserBooks with favorite books (or create those which weren't listed before)
            logger.d(TAG, "Updating UserBooks collection with favorites");
            userBooks = addFavoriteUserBooks(userBooks, user);

            // Store those UserBooks
            if (!userBooks.isEmpty()) {
              logger.d(TAG, "As there are UserBooks for the user, storing those in db");
              final PutAllUserBooksStorageSpec spec = new PutAllUserBooksStorageSpec();
              putAllUserBooksInteractor.execute(spec, userBooks, MoreExecutors.directExecutor()).get();
            }

            // Create UserBooksLikes with liked UserBooks
            logger.d(TAG, "Generating UserBooksLike with liked books");
            final List<UserBookLike> userBookLikes = createUserBooksLikes(user);

            // Store UserBooksLikes
            if (!userBookLikes.isEmpty()) {
              logger.d(TAG, "Storing UserBooksLikes into db");
              final PutAllUserBookLikeStorageSpec userBookLikeStorageSpec =
                  new PutAllUserBookLikeStorageSpec(UserStorageSpecification.UserTarget.ANONYMOUS);
              putAllUserBooksLikesInteractor.execute(userBookLikes, userBookLikeStorageSpec,
                  MoreExecutors.directExecutor()).get();
            }

            // Create all user milestones
            logger.d(TAG, "Creating all UserMilestones");
            List<UserMilestone> milestones =
                createUserMilestonesInteractor.execute(UsersTable.ANONYMOUS_USER_ID, Collections.<Integer>emptyList(), MoreExecutors.directExecutor())
                    .get();

            // Store those in DB
            logger.d(TAG, "Storing all UserMilestones in db");
            final PutUserMilestonesStorageSpec spec1 = new PutUserMilestonesStorageSpec(UserStorageSpecification.UserTarget.ANONYMOUS);
            putAllUserMilestonesInteractor.execute(spec1, milestones, MoreExecutors.directExecutor()).get();

            // Update user milestones with latest status
            logger.d(TAG, "Updating UserMilestones with latest status");
            milestones = updateUserMilestones(milestones, user);

            // Store those on DB (only updated ones)
            if (!milestones.isEmpty()) {
              logger.d(TAG, "Store updated UserMilestones in db");
              putAllUserMilestonesInteractor.execute(spec1, milestones, MoreExecutors.directExecutor()).get();
            }

            // Create UserScore
            logger.d(TAG, "Creating UserScore");
            final UserScore score = createUserScore(user);

            // Store UserScore in DB
            logger.d(TAG, "Storing UserScore in db");
            final UserStorageSpecification target = UserStorageSpecification.target(UserStorageSpecification.UserTarget.ANONYMOUS);
            addUserScoreInteractor.execute(score.getScore(), false, true, target, MoreExecutors.directExecutor()).get();

            // Clean this DB
            logger.d(TAG, "Deleting old db");
            oldDBFile.delete();

            logger.d(TAG, "Everything went OK");
            return true;
          }
        } else {
          logger.d(TAG, "No old database found! Ignoring process!");
          return true;
        }
      }
    };
  }

  private UserScore createUserScore(final UserEntity user) {
    return new UserScore.Builder().setCreatedAt(new Date()).setScore(user.userScore).setUserId(UsersTable.ANONYMOUS_USER_ID).build();
  }

  private List<UserBookLike> createUserBooksLikes(final UserEntity user) {
    final List<String> favoritesBooks =
        user.favoritesBooks != null ? user.favoritesBooks : new ArrayList<String>();
    //UsersTable.ANONYMOUS_USER_ID

    final List<UserBookLike> userBookLikes = new ArrayList<>(favoritesBooks.size());
    for (final String favoriteBookId : favoritesBooks) {
      final UserBookLike userBookLike = new UserBookLike.Builder().withBookId(favoriteBookId)
          .withLiked(true)
          .withUserId(UsersTable.ANONYMOUS_USER_ID)
          .withLikedAt(new Date())
          .withSync(false)
          .build();

      userBookLikes.add(userBookLike);
    }

    //final List<UserBookLike> userBookLikes = new ArrayList<>();
    //for (final UserBook userBook : userBooks) {
    //  final boolean liked = userBook.isLiked();
    //  if (liked) {
    //    final UserBookLike like = new UserBookLike.Builder().withBookId(userBook.getBookId())
    //        .withUserId(userBook.getUserId())
    //        .withLiked(true)
    //        .withLikedAt(new Date())
    //        .build();
    //    userBookLikes.add(like);
    //  }
    //}
    return userBookLikes;
  }

  private List<UserMilestone> updateUserMilestones(final List<UserMilestone> milestones, final UserEntity user) {
    final Set<LevelEntity> levels = user.levels;
    if (levels == null || levels.isEmpty()) {
      return new ArrayList<>();
    }

    final List<UserMilestone> updatedMilestones = new ArrayList<>();

    for (final LevelEntity level : levels) {
      final Set<MilestoneEntity> userMilestones = level.getMilestones();
      for (final MilestoneEntity milestone : userMilestones) {
        final String milestoneId = String.valueOf(milestone.getId());

        for (final UserMilestone userMilestone : milestones) {
          if (milestoneId.equals(userMilestone.getMilestoneId())) {
            final MilestoneEntity.State state = milestone.getState();
            final int userMilestoneState;
            switch (state) {
              case PENDING:
              default:
                userMilestoneState = UserMilestone.STATE_PENDING;
                break;
              case IN_PROGRESS:
                userMilestoneState = UserMilestone.STATE_IN_PROGRESS;
                break;
              case DONE:
                userMilestoneState = UserMilestone.STATE_DONE;
                break;
            }

            final UserMilestone updatedUserMilestone = new UserMilestone.Builder().withUserId(UsersTable.ANONYMOUS_USER_ID)
                .withMilestoneId(String.valueOf(milestoneId))
                .withState(userMilestoneState)
                .withScore(milestone.getPoints())
                .withCreatedAt(new Date())
                .withUpdatedAt(new Date())
                .build();
            updatedMilestones.add(updatedUserMilestone);
          }
        }
      }
    }

    return updatedMilestones;
  }

  private List<UserBook> createReadUserBooks(final UserEntity user) {
    final Map<Integer, List<String>> finishedBooks = user.finishedBooks != null ? user.finishedBooks : new HashMap<Integer, List<String>>();
    final Set<Map.Entry<Integer, List<String>>> entries = finishedBooks.entrySet();

    final Set<UserBook> userBooks = new HashSet<>();

    for (final Map.Entry<Integer, List<String>> entry : entries) {
      final List<String> value = entry.getValue();
      for (final String bookId : value) {
        final UserBook userBook = new UserBook.Builder().setUserId(UsersTable.ANONYMOUS_USER_ID)
            .setBookId(bookId)
            .setCreatedAt(new Date())
            .setUpdatedAt(new Date())
            .setFinished(true)
            .build();
        userBooks.add(userBook);
      }
    }

    return new ArrayList<>(userBooks);
  }

  private List<UserBook> addFavoriteUserBooks(final List<UserBook> books, final UserEntity user) {
    final List<String> favoritesBooks = user.favoritesBooks != null ? user.favoritesBooks : new ArrayList<String>();

    final Set<UserBook> userBooks = new HashSet<>();

    for (final String bookId : favoritesBooks) {

      boolean updated = false;
      for (final UserBook userBook : books) {
        if (userBook.getBookId().equals(bookId)) {
          final UserBook updatedUserBook = new UserBook.Builder(userBook).setInMyBooks(true).build();
          updated = true;
          userBooks.add(updatedUserBook);
          break;
        }
      }

      if (!updated) {
        final UserBook userBook = new UserBook.Builder().setUserId(UsersTable.ANONYMOUS_USER_ID)
            .setBookId(bookId)
            .setCreatedAt(new Date())
            .setUpdatedAt(new Date())
            .setInMyBooks(true)
            .build();
        userBooks.add(userBook);
      }

    }

    return new ArrayList<>(userBooks);
  }

  private static class UserEntity {

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
    public Set<LevelEntity> levels = new LinkedHashSet<>();
    public int userScore;

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
      } catch (Exception e) {
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

      final User2 user2 = new User2.Builder().setId(String.valueOf(raw.id))
          .setName(raw.name)
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
