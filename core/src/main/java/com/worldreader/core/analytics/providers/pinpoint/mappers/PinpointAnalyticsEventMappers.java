package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.worldreader.core.analytics.event.AnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookDetailAnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookFinishedAnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookOpenAnalyticsEvent;
import com.worldreader.core.analytics.event.books.BookReadAnalyticsEvent;
import com.worldreader.core.analytics.event.categories.CategorySelectedAnalyticsEvent;
import com.worldreader.core.analytics.event.other.ChangeLanguageAnalyticsEvent;
import com.worldreader.core.analytics.event.other.MoreBooksAnalyticsEvent;
import com.worldreader.core.analytics.event.other.ScreenNameAnalyticsEvent;
import com.worldreader.core.analytics.event.register.CampaignAnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignInAnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignOutAnalyticsEvent;
import com.worldreader.core.analytics.event.register.SignUpAnalyticsEvent;
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
          put(ChangeLanguageAnalyticsEvent.class, new PinpointReadInLanguageMapper(analyticsClient));
          put(CampaignAnalyticsEvent.class, new PinpointCampaignMapper(analyticsClient));
          //put(SetUserIdAnalyticsEvent.class, NONE);
        }};

    return Collections.unmodifiableMap(mappers);
  }

  @Override public PinpointAnalyticsMapper<?> obtain(Class<?> clazz) {
    return mappers.get(clazz);
  }
}
