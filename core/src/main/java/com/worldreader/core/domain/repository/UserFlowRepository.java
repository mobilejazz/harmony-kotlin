package com.worldreader.core.domain.repository;

import com.worldreader.core.domain.model.UserFlow;

import java.util.List;

public interface UserFlowRepository {

  List<UserFlow> getUserFlow(UserFlow.Type type);

  void update(UserFlow.Type type, List<UserFlow> values);
}
