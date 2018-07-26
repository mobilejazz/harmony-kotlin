package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookDetailAnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookFinishedAnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookOpenAnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookReadAnalyticsEvent;
import com.worldreader.core.analytics.event.categories.CategorySelectedAnalyticsEvent;
import com.worldreader.core.analytics.event.other.AgeSelectedAnalyticsEvent;
import com.worldreader.core.analytics.event.other.ChangeReadingLanguageAnalyticsEvent;
import com.worldreader.core.analytics.event.other.MoreBooksAnalyticsEvent;
import com.worldreader.core.analytics.event.other.ScreenNameAnalyticsEvent;
import com.worldreader.core.analytics.event.other.UILanguageAnalyticsEvent;
import com.worldreader.core.analytics.event.reader.ReaderChangeFontSizeAnalyticsEvent;
import com.worldreader.core.analytics.event.reader.ReaderChangeFontTypeAnalyticsEvent;
import com.worldreader.core.analytics.event.reader.ReaderImageZoomAnalyticsEvent;
import com.worldreader.core.analytics.event.reader.ReaderOpenReaderOptionsAnalyticsEvent;
import com.worldreader.core.analytics.event.register.AcceptPrivacyAnalyticEvent;
import com.worldreader.core.analytics.event.register.CampaignAnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignInAnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignOutAnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignUpAnalyticsEvent;
import com.worldreader.core.analytics.event.search.SearchAnalyticsEvent;
import com.worldreader.core.analytics.mapper.AnalyticsEventMappers;

import java.util.*;

public class PinpointAnalyticsEventMappers implements AnalyticsEventMappers<PinpointAnalyticsMapper<? extends AnalyticsEvent>> {

  public static final PinpointAnalyticsMapper<AnalyticsEvent> NONE = new PinpointAnalyticsMapper<AnalyticsEvent>() {
    @Override public com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent transform(AnalyticsEvent event) {
      return null;
    }
  };

  private final Map<Class<?>, PinpointAnalyticsMapper<? extends AnalyticsEvent>> mappers;


  public PinpointAnalyticsEventMappers(AnalyticsClient analyticsClient) {
    this.mappers = createMappers(analyticsClient);
  }

  private Map<Class<?>, PinpointAnalyticsMapper<? extends AnalyticsEvent>> createMappers(final AnalyticsClient analyticsClient) {

    final Map<Class<?>, PinpointAnalyticsMapper<? extends AnalyticsEvent>> mappers =
        new HashMap<Class<?>, PinpointAnalyticsMapper<? extends AnalyticsEvent>>() {{
          put(BookReadAnalyticsEvent.class, new PinpointBookReadMapper(analyticsClient));
          put(CategorySelectedAnalyticsEvent.class, new PinpointCategoryDetailsMapper(analyticsClient));
          put(BookDetailAnalyticsEvent.class, new PinpointBookDetailsMapper(analyticsClient));
          put(BookOpenAnalyticsEvent.class, new PinpointBookOpenMapper(analyticsClient));
          put(BookFinishedAnalyticsEvent.class, new PinpointBookFinishedMapper(analyticsClient));
          put(SignInAnalyticsEvent.class, new PinpointSignInMapper(analyticsClient));
          put(SignUpAnalyticsEvent.class, new PinpointSignUpMapper(analyticsClient));
          put(ScreenNameAnalyticsEvent.class, new PinpointInScreenMapper(analyticsClient));
          put(MoreBooksAnalyticsEvent.class, new PinpointMoreBooksMapper(analyticsClient));
          put(SignOutAnalyticsEvent.class, new PinpointSignOutMapper(analyticsClient));
          put(ChangeReadingLanguageAnalyticsEvent.class, new PinpointReadInLanguageMapper(analyticsClient));
          put(UILanguageAnalyticsEvent.class, new PinpointUILanguageMapper(analyticsClient));
          put(CampaignAnalyticsEvent.class, new PinpointCampaignMapper(analyticsClient));
          put(SearchAnalyticsEvent.class, new PinpointSearchMapper(analyticsClient));
          put(AgeSelectedAnalyticsEvent.class, new PinpointAgeSelectedMapper(analyticsClient));
          put(ReaderChangeFontTypeAnalyticsEvent.class, new PinpointReaderChangeFontMapper(analyticsClient));
          put(ReaderChangeFontSizeAnalyticsEvent.class, new PinpointReaderChangeFontSizeMapper(analyticsClient));
          put(ReaderOpenReaderOptionsAnalyticsEvent.class, new PinpointReaderOpenReaderOptionsMapper(analyticsClient));
          put(ReaderImageZoomAnalyticsEvent.class, new PinpointReaderImageZoomMapper(analyticsClient));
          put(AcceptPrivacyAnalyticEvent.class, new PinpointAcceptPrivacyMapper(analyticsClient));
          //put(SetUserIdAnalyticsEvent.class, NONE);
        }};

    return Collections.unmodifiableMap(mappers);
  }

  @Override public PinpointAnalyticsMapper<?> obtain(Class<?> clazz) {
    return mappers.get(clazz);
  }
}
