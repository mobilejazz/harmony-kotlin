package com.worldreader.core.datasource.connector.actions;

import com.worldreader.core.datasource.helper.Action;

import javax.inject.Inject;

public class CompleteOnboardingActionFake implements Action<Void, Boolean> {

  @Inject public CompleteOnboardingActionFake() {
  }

  @Override public Boolean perform(final Void value) {
    return true;
  }
}
