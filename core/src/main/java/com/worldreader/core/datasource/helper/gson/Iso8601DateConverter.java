package com.worldreader.core.datasource.helper.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Iso8601DateConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

  public static final Type TYPE = new TypeToken<Date>() {
  }.getType();

  private static final DateFormat ISO_DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()) {{
        setTimeZone(TimeZone.getTimeZone("UTC"));
      }};

  private static final DateFormat ISO_DATE_FORMAT_NO_MILLISECONDS =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()) {{
        setTimeZone(TimeZone.getTimeZone("UTC"));
      }};

  public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(ISO_DATE_FORMAT.format(src));
  }

  public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (!(json instanceof JsonPrimitive)) {
      throw new JsonParseException("The date should be a string value");
    }
    final String dateString = json.getAsString();
    return deserializeToDate(dateString);
  }

  private Date deserializeToDate(String dateString) {
    try {
      return ISO_DATE_FORMAT.parse(dateString);
    } catch (ParseException e) {
      try {
        return ISO_DATE_FORMAT_NO_MILLISECONDS.parse(dateString);
      } catch (ParseException e1) {
        throw new JsonSyntaxException(
            "This date does not implement ISO 8601 standard: " + dateString, e);
      }
    }
  }
}
