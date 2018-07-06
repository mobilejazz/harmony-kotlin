package com.worldreader.core.analytics.mapper;

public interface AnalyticsEventMappers<T extends AnalyticsMapper> {

  T obtain(Class<?> clazz);

}
