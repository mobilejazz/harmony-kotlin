package com.worldreader.core.datasource.repository.spec;

import com.google.common.base.Preconditions;

public abstract class RepositorySpecification {

  public abstract String getIdentifier();

  public static class SimpleRepositorySpecification extends RepositorySpecification {

    private final String identifier;

    public SimpleRepositorySpecification(String identifier) {
      this.identifier = Preconditions.checkNotNull(identifier);
    }

    @Override public String getIdentifier() {
      return this.identifier;
    }
  }

}
