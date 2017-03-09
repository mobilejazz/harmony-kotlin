package com.worldreader.core.datasource.connector.actions;

import com.worldreader.core.datasource.helper.Action;

import javax.inject.Inject;

public class DeleteOnboardingActionFake implements Action<Void> {

  @Inject public DeleteOnboardingActionFake() {
  }

  @Override public boolean perform(final Void value) {
    return true;
  }
}
