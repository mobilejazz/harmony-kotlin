package com.worldreader.core.datasource.helper;

import com.worldreader.core.datasource.model.UserFlowEntity;

import java.util.*;

public class UserFlowEntityFactory {

  private UserFlowEntityFactory() {
    throw new AssertionError("No instances of this class are allowed!");
  }

  public static Set<UserFlowEntity> createUserFlow(UserFlowEntity.Type type) {
    switch (type) {
      case MY_LIBRARY:
        return createMyLibraryUserFlow();
      case READER:
        return createReaderUserFlow();
    }

    throw new IllegalArgumentException("Invalid UserFlowEntity.Type passed: " + type);
  }

  private static Set<UserFlowEntity> createMyLibraryUserFlow() {
    Set<UserFlowEntity> set = new LinkedHashSet<>();
    set.add(userFlow(UserFlowEntity.Type.MY_LIBRARY, UserFlowEntity.PHASE.MY_LIBRARY_HOME));
    set.add(userFlow(UserFlowEntity.Type.MY_LIBRARY, UserFlowEntity.PHASE.MY_LIBRARY_COLLECTIONS));
    set.add(userFlow(UserFlowEntity.Type.MY_LIBRARY, UserFlowEntity.PHASE.MY_LIBRARY_CATEGORIES));
    return set;
  }

  private static Set<UserFlowEntity> createReaderUserFlow() {
    Set<UserFlowEntity> set = new LinkedHashSet<>();
    set.add(userFlow(UserFlowEntity.Type.READER, UserFlowEntity.PHASE.READER_READY_TO_READ));
    set.add(userFlow(UserFlowEntity.Type.READER, UserFlowEntity.PHASE.READER_READING_OPTIONS));
    set.add(userFlow(UserFlowEntity.Type.READER, UserFlowEntity.PHASE.READER_SPECIFIC_PAGE));
    set.add(userFlow(UserFlowEntity.Type.READER, UserFlowEntity.PHASE.READER_SET_YOUR_GOALS));
    set.add(userFlow(UserFlowEntity.Type.READER, UserFlowEntity.PHASE.READER_BECOME_A_WORLDREADER));
    return set;
  }

  private static UserFlowEntity userFlow(UserFlowEntity.Type type, int phase) {
    return UserFlowEntity.create(type, phase, false);
  }
}
