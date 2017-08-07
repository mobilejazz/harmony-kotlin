package com.worldreader.core.datasource.mapper;

import android.support.annotation.NonNull;
import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.BookEntity;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.Category;

import java.util.*;

public class BookEntityDataMapper implements Mapper<Book, BookEntity> {

  private String endpoint;

  public BookEntityDataMapper(String endpoint) {
    this.endpoint = endpoint;
  }

  @Override public Book transform(BookEntity bookEntity) {
    if (bookEntity == null) {
      //throw new IllegalArgumentException("BookEntity must be not null");
      return null;
    }

    Book book = new Book();
    book.setId(bookEntity.getId());
    book.setVersion(bookEntity.getVersion());
    book.setLanguage(bookEntity.getLanguage());
    book.setTitle(bookEntity.getTitle());
    book.setAuthor(bookEntity.getAuthor());
    book.setPublisher(bookEntity.getPublisher());
    book.setRights(bookEntity.getRights());
    book.setPublished(bookEntity.getPublished());
    book.setDescription(bookEntity.getDescription());
    book.setScore(bookEntity.getScore());
    book.setRatings(bookEntity.getRatings());
    book.setCover(bookEntity.getCover());
    book.setContent(bookEntity.getContent());
    book.setSize(bookEntity.getSize());
    book.setOpens(bookEntity.getOpens());
    book.setCategories(getCategories(bookEntity));
    book.setAvailableToOfflineMode(bookEntity.isAvalableToOfflineMode());

    return book;
  }

  @Override public List<Book> transform(List<BookEntity> bookEntities) {
    List<Book> books = new ArrayList<>();

    if (bookEntities == null) {
      return books;
    }

    for (BookEntity bookEntity : bookEntities) {
      Book book = transform(bookEntity);
      books.add(book);
    }

    return books;
  }

  @Override public BookEntity transformInverse(Book data) {
    if (data == null) {
      throw new IllegalArgumentException("Book == null");
    }

    BookEntity bookEntity = new BookEntity();
    bookEntity.setId(data.getId());
    bookEntity.setVersion(data.getVersion());
    bookEntity.setLanguage(data.getLanguage());
    bookEntity.setTitle(data.getTitle());
    bookEntity.setAuthor(data.getAuthor());
    bookEntity.setPublisher(data.getPublisher());
    bookEntity.setRights(data.getRights());
    bookEntity.setPublished(data.getPublished());
    bookEntity.setDescription(data.getDescription());
    bookEntity.setScore(data.getScore());
    bookEntity.setRatings(data.getRatings());
    bookEntity.setCover(bookEntity.getCover());
    bookEntity.setContent(data.getContent());
    bookEntity.setSize(data.getSize());
    bookEntity.setOpens(data.getOpens());
    bookEntity.setCategories(getCategories(data));
    bookEntity.setCategoryNames(getCategoriesName(data));
    bookEntity.setAvalableToOfflineMode(data.isAvailableToOfflineMode());

    return bookEntity;
  }

  @Override public List<BookEntity> transformInverse(List<Book> data) {
    throw new IllegalStateException("transformInverse(List<Book> data) is not supported");
  }

  @NonNull private List<Category> getCategories(BookEntity bookEntity) {
    List<Category> categories = new ArrayList<>();
    List<Integer> categoriesId = bookEntity.getCategories();
    if (categoriesId != null && categoriesId.size() > 0) {
      for (Integer categoryId : categoriesId) {
        for (String categoryKey : bookEntity.getCategoryNames().keySet()) {
          if (categoryId == Integer.parseInt(categoryKey)) {
            Category category = new Category();
            category.setId(categoryId);
            category.setTitle(bookEntity.getCategoryNames().get(categoryKey));
            categories.add(category);
            break;
          }
        }
      }
    }
    return categories;
  }

  private HashMap<String, String> getCategoriesName(Book data) {
    HashMap<String, String> categoriesName = new HashMap<>();
    List<Category> categories = data.getCategories();

    for (Category category : categories) {
      categoriesName.put(String.valueOf(category.getId()), category.getTitle());
    }

    return categoriesName;
  }

  @NonNull private List<Integer> getCategories(Book book) {
    List<Integer> categoriesId = new ArrayList<>();

    List<Category> categories = book.getCategories();

    if (categories != null && categories.size() > 0) {
      for (Category category : categories) {
        categoriesId.add(category.getId());
      }
    }

    return categoriesId;
  }
}
