package com.worldreader.core.datasource.repository;

import com.google.common.base.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.model.RepositoryModel;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import java.util.Collection;

public interface Repository<M extends RepositoryModel, S extends RepositorySpecification> {

  void get(S specification, Callback<Optional<M>> callback);

  void getAll(S specification, Callback<Optional<Collection<M>>> callback);

  void put(M m, S specification, Callback<Optional<M>> callback);

  void putAll(Collection<M> ms, S specification, Callback<Optional<Collection<M>>> callback);

  void remove(M m, S specification, Callback<Optional<M>> callback);

  void removeAll(Collection<M> ms, S specification, Callback<Optional<Collection<M>>> callback);

  interface Storage<M extends RepositoryModel, S extends RepositorySpecification> extends
      Repository<M, S> {

  }

  interface Network<M extends RepositoryModel, S extends RepositorySpecification> extends
      Repository<M, S> {

  }

}

