package com.worldreader.core.datasource.model;

public class UserFlowEntity {

  private Type type;
  private int phase;
  private boolean isDisplayed;

  private UserFlowEntity(Type type, int phase, boolean isDisplayed) {
    this.type = type;
    this.phase = phase;
    this.isDisplayed = isDisplayed;
  }

  public static UserFlowEntity create(Type type, int phase, boolean isDisplayed) {
    return new UserFlowEntity(type, phase, isDisplayed);
  }

  public Type getType() {
    return type;
  }

  public int getPhase() {
    return phase;
  }

  public boolean isDisplayed() {
    return isDisplayed;
  }

  public void setIsDisplayed(boolean isDisplayed) {
    this.isDisplayed = isDisplayed;
  }

  public enum Type {
    MY_LIBRARY,
    READER
  }

  public static class PHASE {

    // My Library
    public static final int MY_LIBRARY_HOME = 1000;
    public static final int MY_LIBRARY_COLLECTIONS = 1001;
    public static final int MY_LIBRARY_CATEGORIES = 1002;

    // Reader
    public static final int READER_READY_TO_READ = 2001;
    public static final int READER_READING_OPTIONS = 2002;
    public static final int READER_SPECIFIC_PAGE = 2003;
    public static final int READER_SET_YOUR_GOALS = 2004;
    public static final int READER_BECOME_A_WORLDREADER = 2005;
  }
}
