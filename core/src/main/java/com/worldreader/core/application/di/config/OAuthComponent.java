package com.worldreader.core.application.di.config;

import com.worldreader.core.domain.repository.OAuthRepository;

public interface OAuthComponent {

  OAuthRepository oAuthRepository();

}
