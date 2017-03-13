package com.worldreader.core.datasource.storage.datasource.userflow;

import com.worldreader.core.datasource.model.UserFlowEntity;

import java.util.List;

public interface UserFlowBdDataSource {

  void persist(UserFlowEntity.Type type, List<UserFlowEntity> values);

  List<UserFlowEntity> getUserFlow(UserFlowEntity.Type type);
}
