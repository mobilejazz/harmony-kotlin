package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.model.BannerEntity;
import com.worldreader.core.domain.model.Banner;

import javax.inject.Inject;
import java.util.*;

public class BannerEntityDataMapper implements Mapper<Banner, BannerEntity> {

  private final String worldreaderApi;
  private final BookEntityDataMapper bookEntityDataMapper;

  @Inject
  public BannerEntityDataMapper(String worldreaderApi, BookEntityDataMapper bookEntityDataMapper) {
    this.worldreaderApi = worldreaderApi;
    this.bookEntityDataMapper = bookEntityDataMapper;
  }

  @Override public Banner transform(BannerEntity bannerEntity) {
    if (bannerEntity == null) {
      throw new IllegalArgumentException("BannerEntity must be not null");
    }

    Banner banner = new Banner();
    banner.setId(bannerEntity.getId());
    banner.setName(bannerEntity.getName());
    banner.setStart(bannerEntity.getStart());
    banner.setEnd(bannerEntity.getEnd());
    banner.setImage(bannerEntity.getImage());
    banner.setType(bannerEntity.getType());
    banner.setBooks(bookEntityDataMapper.transform(bannerEntity.getBookEntities()));

    return banner;
  }

  @Override public List<Banner> transform(List<BannerEntity> bannerEntities) {
    if (bannerEntities == null) {
      throw new IllegalArgumentException("List<BannerEntity> must be not null");
    }

    List<Banner> banners = new ArrayList<>();
    for (BannerEntity bannerEntity : bannerEntities) {
      Banner banner = transform(bannerEntity);
      banners.add(banner);
    }

    return banners;
  }

  @Override public BannerEntity transformInverse(Banner data) {
    throw new IllegalStateException("transformInverse(Banner data) is not supported");
  }

  @Override public List<BannerEntity> transformInverse(List<Banner> data) {
    throw new IllegalStateException("transformInverse(List<Banner> data) is not supported");
  }
}
