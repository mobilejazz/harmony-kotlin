package com.worldreader.core.datasource.connector.actions;

import com.worldreader.core.datasource.helper.Action;

import javax.inject.Inject;

public class CompleteOnboardingActionFake implements Action<Void> {

  @Inject public CompleteOnboardingActionFake() {
  }

  @Override public boolean perform(final Void value) {
    return true;
  }
}
