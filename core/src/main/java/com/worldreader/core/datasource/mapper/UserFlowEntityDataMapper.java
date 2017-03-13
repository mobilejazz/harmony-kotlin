package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.UserFlowEntity;
import com.worldreader.core.domain.model.UserFlow;

import java.util.ArrayList;
import java.util.List;

public class UserFlowEntityDataMapper implements Mapper<UserFlow, UserFlowEntity> {
  @Override public UserFlow transform(UserFlowEntity data) {
    return UserFlow.create(transformTypes(data.getType()), data.getPhase(), data.isDisplayed());
  }

  @Override public List<UserFlow> transform(List<UserFlowEntity> data) {
    List<UserFlow> userFlows = new ArrayList<>(data.size());

    for (UserFlowEntity userFlowEntity : data) {
      userFlows.add(transform(userFlowEntity));
    }

    return userFlows;
  }

  @Override public UserFlowEntity transformInverse(UserFlow data) {
    return UserFlowEntity.create(transformTypes(data.getType()), data.getPhase(),
        data.isDisplayed());
  }

  @Override public List<UserFlowEntity> transformInverse(List<UserFlow> data) {
    List<UserFlowEntity> userFlowEntities = new ArrayList<>(data.size());

    for (UserFlow userFlow : data) {
      userFlowEntities.add(transformInverse(userFlow));
    }

    return userFlowEntities;
  }

  public UserFlow.Type transformTypes(UserFlowEntity.Type type) {
    switch (type) {
      case MY_LIBRARY:
        return UserFlow.Type.MY_LIBRARY;
      case READER:
        return UserFlow.Type.READER;
    }

    throw new IllegalArgumentException("UserFlowEntity.Type type is not OK");
  }

  public UserFlowEntity.Type transformTypes(UserFlow.Type type) {
    switch (type) {
      case MY_LIBRARY:
        return UserFlowEntity.Type.MY_LIBRARY;
      case READER:
        return UserFlowEntity.Type.READER;
    }

    throw new IllegalArgumentException("UserFlow.Type type is not OK");
  }
}
