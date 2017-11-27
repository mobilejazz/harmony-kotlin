package com.worldreader.core.datasource;

import com.worldreader.core.datasource.helper.UserFlowEntityFactory;
import com.worldreader.core.datasource.mapper.UserFlowEntityDataMapper;
import com.worldreader.core.datasource.model.UserFlowEntity;
import com.worldreader.core.datasource.storage.datasource.userflow.UserFlowBdDataSource;
import com.worldreader.core.domain.model.UserFlow;
import com.worldreader.core.domain.repository.UserFlowRepository;

import javax.inject.Inject;
import java.util.*;

public class UserFlowDataSource implements UserFlowRepository {

  private UserFlowBdDataSource bd;
  private UserFlowEntityDataMapper mapper;

  @Inject public UserFlowDataSource(UserFlowBdDataSource bd, UserFlowEntityDataMapper mapper) {
    this.bd = bd;
    this.mapper = mapper;
  }

  @Override public List<UserFlow> getUserFlow(UserFlow.Type type) {
    UserFlowEntity.Type userFlowEntityType = mapper.transformTypes(type);

    List<UserFlowEntity> userFlowEntities = bd.getUserFlow(userFlowEntityType);

    if (userFlowEntities == null) {
      Set<UserFlowEntity> userFlow = UserFlowEntityFactory.createUserFlow(userFlowEntityType);

      userFlowEntities = new ArrayList<>(userFlow.size());
      userFlowEntities.addAll(userFlow);

      bd.persist(userFlowEntityType, userFlowEntities);
    }

    return mapper.transform(userFlowEntities);
  }

  @Override public void update(UserFlow.Type type, List<UserFlow> values) {
    UserFlowEntity.Type userFlowEntityType = mapper.transformTypes(type);
    List<UserFlowEntity> userFlowEntities = mapper.transformInverse(values);
    bd.persist(userFlowEntityType, userFlowEntities);
  }
}
