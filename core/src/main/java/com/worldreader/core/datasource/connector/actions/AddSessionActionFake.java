package com.worldreader.core.datasource.connector.actions;

import com.worldreader.core.datasource.helper.Action;

import javax.inject.Inject;
import java.util.*;

public class AddSessionActionFake implements Action<Date, Boolean> {

  @Inject public AddSessionActionFake() {
  }

  @Override public Boolean perform(final Date value) {
    return true;
  }
}
