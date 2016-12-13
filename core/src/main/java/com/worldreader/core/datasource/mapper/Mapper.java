package com.worldreader.core.datasource.mapper;

public interface Mapper<From, To> {

  To transform(From from);

}
