package com.worldreader.core.datasource.connector.actions;

import com.worldreader.core.datasource.helper.Action;

import javax.inject.Inject;

public class DeleteOnboardingActionFake implements Action<Void, Boolean> {

  @Inject public DeleteOnboardingActionFake() {
  }

  @Override public Boolean perform(final Void value) {
    return true;
  }
}
