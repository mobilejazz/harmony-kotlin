package com.worldreader.core.datasource.connector.actions;

import com.worldreader.core.datasource.helper.Action;

import javax.inject.Inject;

public class CompleteUserGoalsActionFake implements Action<Boolean, Boolean> {

  @Inject public CompleteUserGoalsActionFake() {
  }

  @Override public Boolean perform(final Boolean value) {
    return true;
  }
}
