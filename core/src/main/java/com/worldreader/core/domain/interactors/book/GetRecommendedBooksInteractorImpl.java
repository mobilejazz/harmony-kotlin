package com.worldreader.core.domain.interactors.book;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.common.deprecated.error.ErrorCore;
import com.worldreader.core.domain.deprecated.AbstractInteractor;
import com.worldreader.core.domain.deprecated.DomainCallback;
import com.worldreader.core.domain.deprecated.executor.InteractorExecutor;
import com.worldreader.core.domain.helper.DefaultValues;
import com.worldreader.core.domain.model.Book;
import com.worldreader.core.domain.model.BookSort;
import com.worldreader.core.domain.model.Category;
import com.worldreader.core.domain.repository.BookRepository;
import com.worldreader.core.domain.thread.MainThread;

import javax.inject.Inject;
import java.util.*;

public class GetRecommendedBooksInteractorImpl extends AbstractInteractor<List<Book>, ErrorCore>
    implements GetRecommendedBooksInteractor {

  private BookRepository bookRepository;

  private int offset;
  private int limit;
  private Book book;
  private DomainCallback<List<Book>, ErrorCore> callback;

  @Inject
  public GetRecommendedBooksInteractorImpl(InteractorExecutor executor, MainThread mainThread,
      BookRepository bookRepository) {
    super(executor, mainThread);
    this.bookRepository = bookRepository;
  }

  @Override public void execute(int offset, int limit, Book book,
      DomainCallback<List<Book>, ErrorCore> callback) {
    this.offset = offset;
    this.limit = limit;
    this.book = book;
    this.callback = callback;
    this.executor.run(this);
  }

  @Override public void run() {
    final List<BookSort> bookSorts =
        Arrays.asList(BookSort.createBookSort(BookSort.Type.OPENS, BookSort.Value.DESC),
            BookSort.createBookSort(BookSort.Type.DATE, BookSort.Value.DESC));

    String language = DefaultValues.DEFAULT_LANGUAGE;

    List<Integer> categoriesIds = new ArrayList<>(book.getCategories().size());
    for (Category category : book.getCategories()) {
      categoriesIds.add(category.getId());
    }

    bookRepository.books(categoriesIds, null /*list*/, bookSorts, false/*open country*/, language,
        offset/*index*/, limit/*limit*/, new CompletionCallback<List<Book>>() {
          @Override public void onSuccess(List<Book> books) {
            performSuccessCallback(callback, books);
          }

          @Override public void onError(ErrorCore errorCore) {
            performErrorCallback(callback, errorCore);
          }
        });
  }
}
