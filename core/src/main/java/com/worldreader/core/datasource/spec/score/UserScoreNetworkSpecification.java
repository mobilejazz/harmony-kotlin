package com.worldreader.core.datasource.spec.score;

import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

public abstract class UserScoreNetworkSpecification extends RepositorySpecification {

  @Override public String getIdentifier() {
    return this.getClass().getSimpleName();
  }
}
