package com.worldreader.core.datasource.repository;

import com.memoizrlabs.retrooptional.Optional;
import com.worldreader.core.common.callback.Callback;
import com.worldreader.core.datasource.repository.model.RepositoryModel;
import com.worldreader.core.datasource.repository.spec.RepositorySpecification;

import java.util.*;

public interface Repository<M extends RepositoryModel, S extends RepositorySpecification> {

  void get(Callback<Optional<M>> callback);

  void getAll(Callback<Optional<Collection<M>>> callback);

  void get(S specification, Callback<Optional<M>> callback);

  void getAll(S specification, Callback<Optional<Collection<M>>> callback);

  void put(M model, Callback<Optional<M>> callback);

  void putAll(Collection<M> models, Callback<Optional<Collection<M>>> callback);

  void put(M model, S specification, Callback<Optional<M>> callback);

  void putAll(Collection<M> models, S specification, Callback<Optional<M>> callback);

  void remove(M model, Callback<Optional<M>> callback);

  void remove(Collection<M> models, Callback<Optional<Boolean>> callback);

  void remove(M model, S specification, Callback<Optional<M>> callback);

  void remove(Collection<M> models, S specification, Callback<Optional<M>> callback);

  void removeAll(Callback<Optional<M>> callback);

  interface Storage<M extends RepositoryModel, S extends RepositorySpecification> extends
      Repository<M, S> {

  }

  interface Network<M extends RepositoryModel, S extends RepositorySpecification> extends
      Repository<M, S> {

  }

}

