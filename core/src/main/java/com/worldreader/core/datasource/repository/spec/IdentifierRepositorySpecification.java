package com.worldreader.core.datasource.repository.spec;

public class IdentifierRepositorySpecification extends RepositorySpecification {

  private final String identifier;

  public IdentifierRepositorySpecification(String identifier) {
    this.identifier = identifier;
  }

  @Override public String getIdentifier() {
    return identifier;
  }
}