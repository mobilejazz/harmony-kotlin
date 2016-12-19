package com.worldreader.core.domain.repository;

import com.worldreader.core.common.deprecated.callback.CompletionCallback;
import com.worldreader.core.domain.model.Banner;

import java.util.*;

public interface BannerRepository {

  void mainBanner(int index, int limit, CompletionCallback<List<Banner>> callback);

  void collectionBanner(int index, int limit, CompletionCallback<List<Banner>> callback);
}
