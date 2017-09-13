package com.worldreader.core.datasource.network.general.retrofit.converter;

import com.worldreader.core.datasource.network.general.retrofit.annotations.XML;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class JsonXmlConverterFactory extends Converter.Factory {

  private final Converter.Factory gson;
  private final Converter.Factory xml;

  public JsonXmlConverterFactory(final GsonConverterFactory gsonFactory, final SimpleXmlConverterFactory xmlFactory) {
    this.gson = gsonFactory;
    this.xml = xmlFactory;
  }

  @Nullable @Override
  public Converter<ResponseBody, ?> responseBodyConverter(final Type type, final Annotation[] annotations, final Retrofit retrofit) {
    for (final Annotation annotation : annotations) {
      if (annotation.annotationType() == XML.class) {
        return xml.responseBodyConverter(type, annotations, retrofit);
      }
    }
    return gson.responseBodyConverter(type, annotations, retrofit);
  }

  public @Nullable @Override Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] annotations, Annotation[] methodAnnotations,
      Retrofit retrofit) {
    for (final Annotation annotation : annotations) {
      if (annotation.annotationType() == XML.class) {
        return xml.requestBodyConverter(type, annotations, methodAnnotations, retrofit);
      }
    }
    return gson.requestBodyConverter(type, annotations, methodAnnotations, retrofit);
  }

  public Converter.Factory getGson() {
    return gson;
  }

  public Converter.Factory getXml() {
    return xml;
  }
}
