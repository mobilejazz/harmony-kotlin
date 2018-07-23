package com.worldreader.core.analytics.providers.pinpoint.mappers;

import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsEvent;
import com.worldreader.core.analytics.event.AnalyticsEventConstants;
import com.worldreader.core.analytics.event.search.SearchAnalyticsEvent;

public class PinpointSearchMapper implements PinpointAnalyticsMapper<SearchAnalyticsEvent> {
  private final AnalyticsClient ac;

  public PinpointSearchMapper(AnalyticsClient ac) {
    this.ac = ac;
  }

  @Override public AnalyticsEvent transform(SearchAnalyticsEvent event) {
    final AnalyticsEvent analyticsEvent = ac.createEvent(AnalyticsEventConstants.SEARCH_EVENT);
    analyticsEvent.addAttribute(AnalyticsEventConstants.SEARCH_QUERY_ATTRIBUTE,event.getQuery());
    analyticsEvent.addAttribute(AnalyticsEventConstants.COUNTRTY_CODE, event.getCountry());
    if(event.getAges()!=null && !event.getAges().isEmpty()){
      java.util.Collections.sort(event.getAges());
      for(int i=0; i<event.getAges().size(); i++){
        analyticsEvent.addAttribute(AnalyticsEventConstants.SEARCH_AGE_ATTRIBUTE+"_"+i,event.getAges().get(i));
      }
    }
    if(event.getCategories()!=null && !event.getCategories().isEmpty()){
      if(event.getCategories().size()>1) {
        java.util.Collections.sort(event.getCategories());
      }
      for(int i=0; i<event.getCategories().size(); i++){
        analyticsEvent.addAttribute(AnalyticsEventConstants.SEARCH_CATEGORY_ATTRIBUTE+"_"+i,event.getCategories().get(i).toString());
      }
    }
    if(event.getLanguages()!=null && !event.getLanguages().isEmpty()){
      java.util.Collections.sort(event.getLanguages());
      for(int i=0; i<event.getLanguages().size(); i++){
        analyticsEvent.addAttribute(AnalyticsEventConstants.SEARCH_LANG_ATTRIBUTE+"_"+i,event.getLanguages().get(i));
      }
    }
    return analyticsEvent;
  }
}
