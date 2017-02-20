package com.worldreader.core.datasource.repository;

public interface NetworkRepositoryProvider<R extends Repository.Network> {

  R get();

  R getRealNetwork();

  R getFakeNetwork();
}
