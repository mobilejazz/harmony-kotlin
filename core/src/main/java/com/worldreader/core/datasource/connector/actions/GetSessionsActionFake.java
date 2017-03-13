package com.worldreader.core.datasource.connector.actions;

import com.worldreader.core.datasource.helper.Action;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GetSessionsActionFake implements Action<Void, List<Date>> {

  @Inject public GetSessionsActionFake() {
  }

  @Override public List<Date> perform(final Void value) {
    return Collections.emptyList();
  }

}
