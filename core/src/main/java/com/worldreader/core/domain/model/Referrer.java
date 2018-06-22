package com.worldreader.core.domain.model;

import java.util.*;
import java.util.regex.*;

/**
 * Rich model that represent a referrer who invites other users to install the app
 */
public class Referrer {

  public static final String REFERRER_URL_QUERY_KEY = "referrer";

  public static class ReferrerParseException extends Exception {

    private ReferrerParseException(String message) {
      super(message);
    }
  }

  private static final String REFERRER_INVITATION_IDENTIFIER = "wr_"; // Used to differentiate invitations from other possible campaigns
  private static final String REFERRER_DEVICE_ID_KEY = "did";
  private static final String REFERRER_USER_ID_KEY = "uid";
  private static final String REFERRER_VALUE_DELIMITER = "!";
  public static final String KEY_UTM_SOURCE = "utm_source";//required param
  public static final String KEY_UTM_MEDIUM = "utm_medium";
  public static final String KEY_UTM_TERM = "utm_term";
  public static final String KEY_UTM_CONTENT = "utm_content";
  public static final String KEY_UTM_CAMPAIGN = "utm_campaign";
  public static final String KEY_ANID_CAMPAIGN = "anid";//Ad Network Id, required param


  private String deviceId;
  private String userId;
  private Map<String, String> campaign;

  public Referrer(String deviceId, String userId, Map<String, String> campaign) {
    this.deviceId = deviceId;
    this.userId = userId;
    this.campaign = campaign;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getUserId() {
    return userId;
  }

  public Map<String, String> getCampaign() {
    return campaign;
  }

  /**
   * Check if the string has a valid invitation identifier (to be sure that is not part of any other campaign)
   * @param referrerUrlQueryValue
   * @return
   */
  public static boolean isValidQueryValue(String referrerUrlQueryValue) {
    return referrerUrlQueryValue.startsWith(REFERRER_INVITATION_IDENTIFIER);
  }

  /**
   *  Creates a referrer object from the value of a url query field
   * @param referrerUrlQueryValue
   * @return
   * @throws ReferrerParseException fi the string cannot be parsed
   */
  public static Referrer parse(String referrerUrlQueryValue) throws ReferrerParseException {
    if (!referrerUrlQueryValue.startsWith(REFERRER_INVITATION_IDENTIFIER)) {
      throw new ReferrerParseException("The referrer string does not belong to a Worldreader invitation");
    }

    String deviceId = parseValueForInvite(referrerUrlQueryValue, REFERRER_INVITATION_IDENTIFIER+REFERRER_DEVICE_ID_KEY);
    String userId = parseValueForInvite(referrerUrlQueryValue, REFERRER_USER_ID_KEY);
    Map<String, String> campaign = parseUrlForUtmValues(referrerUrlQueryValue);
    if (deviceId == null && userId == null) {
      throw new ReferrerParseException("The string does not contains neither deviceId nor userId in the expected format");
    }
    return new Referrer(deviceId, userId, campaign);
  }

  private static String parseValueForInvite(String referrer, String key) {
    String invite = referrer.substring(referrer.indexOf(REFERRER_INVITATION_IDENTIFIER+REFERRER_DEVICE_ID_KEY), referrer.lastIndexOf("!")+1);
    String value = invite.substring(referrer.indexOf(key)+key.length(), key.equals(REFERRER_USER_ID_KEY) ? referrer.lastIndexOf("!") : referrer.indexOf
        ("!"));
    return value;
  }

  private static Map<String, String> parseUrlForUtmValues(String url){
    if(url.indexOf("!%26")>0){
      url = url.substring(url.indexOf("!%26")+4, url.length());
    }
    Map<String, String> c = new HashMap<>();
    if(url.indexOf("utm_") > -1) {
      String[] values = url.split("%26");
      for (int i = 0; i < values.length; i++) {
        String[] utmParam = values[i].split("%3D");
        c.put(utmParam[0], utmParam[1]);
      }
    }
    return c;
  }

  /**
   * Serlialize the referrer as a url query field
   * @return
   */
  public String formatAsUrlQuery() {
    return new StringBuilder(REFERRER_URL_QUERY_KEY).append("=").append(formatAsUrlQueryValue()).toString();
  }

  /**
   * Serialize the referrer to a string intended to be part on a url query field
   * @return
   */
  public String formatAsUrlQueryValue() {
    StringBuilder builder = new StringBuilder(REFERRER_INVITATION_IDENTIFIER);
    if (deviceId != null) {
      builder.append(REFERRER_DEVICE_ID_KEY).append(deviceId).append(REFERRER_VALUE_DELIMITER);
    }
    if (userId != null) {
      builder.append(REFERRER_USER_ID_KEY).append(userId).append(REFERRER_VALUE_DELIMITER);

    }
    return builder.toString();
  }

}
