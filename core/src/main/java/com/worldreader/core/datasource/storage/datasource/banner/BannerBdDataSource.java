package com.worldreader.core.datasource.storage.datasource.banner;

import com.worldreader.core.datasource.model.BannerEntity;
import com.worldreader.core.datasource.storage.exceptions.InvalidCacheException;

import java.util.*;

public interface BannerBdDataSource {

  List<BannerEntity> obtains(String key) throws InvalidCacheException;

  void persist(String key, List<BannerEntity> banners);
}
