package com.worldreader.core.datasource.mapper;

import com.worldreader.core.datasource.mapper.deprecated.Mapper;
import com.worldreader.core.datasource.network.model.OAuthNetworkResponseEntity;
import com.worldreader.core.domain.model.OAuthResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton public class OAuthEntityDataMapper
    implements Mapper<OAuthResponse, OAuthNetworkResponseEntity> {

  @Inject public OAuthEntityDataMapper() {
  }

  @Override public OAuthResponse transform(OAuthNetworkResponseEntity data) {
    if (data == null) {
      throw new IllegalArgumentException("OAuthNetworkResponseEntity must be not null");
    }

    OAuthResponse oAuthResponse = new OAuthResponse();
    oAuthResponse.setAccessToken(data.getAccessToken());
    oAuthResponse.setExpiresIn(data.getExpiresIn());
    oAuthResponse.setRefreshToken(data.getRefreshToken());
    oAuthResponse.setScope(data.getScope());
    oAuthResponse.setTokenType(data.getTokenType());

    return oAuthResponse;
  }

  @Override public List<OAuthResponse> transform(List<OAuthNetworkResponseEntity> data) {
    throw new IllegalStateException(
        "transform(List<OAuthNetworkResponseEntity> data) not supported");
  }

  @Override public OAuthNetworkResponseEntity transformInverse(OAuthResponse data) {
    throw new IllegalStateException("transformInverse(OAuthResponse data) not supported");
  }

  @Override public List<OAuthNetworkResponseEntity> transformInverse(List<OAuthResponse> data) {
    throw new IllegalStateException("transform(List<OAuthResponse> data) not supported");
  }
}
