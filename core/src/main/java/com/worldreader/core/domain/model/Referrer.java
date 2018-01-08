package com.worldreader.core.domain.model;

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

  private String deviceId;
  private String userId;

  public Referrer(String deviceId, String userId) {
    this.deviceId = deviceId;
    this.userId = userId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getUserId() {
    return userId;
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

    String deviceId = parseValueForKey(referrerUrlQueryValue, REFERRER_DEVICE_ID_KEY);
    String userId = parseValueForKey(referrerUrlQueryValue, REFERRER_USER_ID_KEY);
    if (deviceId == null && userId == null) {
      throw new ReferrerParseException("The string does not contains neither deviceId nor userId in the expected format");
    }
    return new Referrer(deviceId, userId);
  }

  private static String parseValueForKey(String referrer, String key) {
    String userId = null;
    Pattern pattern = Pattern.compile(key + "(.*?)\\!");
    Matcher matcher = pattern.matcher(referrer);
    if (matcher.find()) {
      userId = matcher.group(1);
    }

    return userId;
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
