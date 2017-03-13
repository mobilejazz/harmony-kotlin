package com.worldreader.core.datasource.connector.actions;

import com.worldreader.core.datasource.helper.Action;
import com.worldreader.core.domain.model.Book;

public class PutBookReadingActionFake implements Action<Book, Boolean> {

  @Override public Boolean perform(final Book value) {
    return true;
  }

}
