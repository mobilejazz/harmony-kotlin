package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.analytics.Analytics;
import com.worldreader.core.application.helper.reachability.Reachability;
import com.worldreader.core.common.date.Dates;
import com.worldreader.core.datasource.StreamingBookDataSource;
import com.worldreader.core.domain.interactors.dictionary.GetWordDefinitionInteractor;
import com.worldreader.reader.wr.configuration.ReaderConfig;
import com.worldreader.reader.wr.helper.BrightnessManager;
import com.worldreader.reader.wr.helper.ReaderBookMetadataManager;
import com.worldreader.reader.wr.helper.WasabiManager;

import javax.inject.Inject;

public class DICompanion {

  @Inject public StreamingBookDataSource streamingBookDataSource;
  @Inject public GetWordDefinitionInteractor getWordDefinitionInteractor;

  @Inject public ReaderConfig config;
  @Inject public ReaderBookMetadataManager readerBookMetadataManager;
  @Inject public BrightnessManager brightnessManager;
  @Inject public WasabiManager wasabiManager;
  @Inject public Dates dateUtils;
  @Inject public Reachability reachability;
  @Inject public Analytics analytics;
  @Inject public Logger logger;
}
