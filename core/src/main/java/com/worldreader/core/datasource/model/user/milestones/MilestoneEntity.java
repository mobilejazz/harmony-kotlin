package com.worldreader.core.datasource.model.user.milestones;

public class MilestoneEntity implements Cloneable {

  public enum State {
    PENDING, IN_PROGRESS, DONE
  }

  private final int id;
  private final String description;
  private final int points;
  private final State state;
  private final Metadata metadata;

  public MilestoneEntity(int id, String description, int points, State state, Metadata metadata) {
    this.id = id;
    this.description = description;
    this.points = points;
    this.state = state;
    this.metadata = metadata;
  }

  public static MilestoneEntity updateTo(State state, MilestoneEntity milestone) {
    return new MilestoneEntity(milestone.getId(), milestone.getDescription(), milestone.getPoints(),
        state, milestone.getMetadata());
  }

  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public int getPoints() {
    return points;
  }

  public State getState() {
    return state;
  }

  public Metadata getMetadata() {
    return metadata;
  }

  @Override public String toString() {
    return "MilestoneEntity{"
        + "id="
        + id
        + ", description='"
        + description
        + '\''
        + ", points="
        + points
        + ", state="
        + state
        + '}';
  }

  @Override public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MilestoneEntity milestone = (MilestoneEntity) o;

    return id == milestone.id;
  }

  @Override public int hashCode() {
    return id;
  }

  public static class ID {

    // Visitor
    public static final int VISITOR_SET_YOUR_GOALS = 1001;
    public static final int VISITOR_ACHIEVE_DAILY_GOAL = 1002;
    public static final int VISITOR_READ_ON_2_CONTINUOUS_DAYS = 1003;
    public static final int VISITOR_READ_ON_3_CONTINUOUS_DAYS = 1004;
    public static final int VISITOR_FULFILL_PROFILE_INFORMATION = 1005;
    public static final int VISITOR_LIKE_BOOK = 1008;

    // Avid Reader
    public static final int AVID_READER_USE_TTS = 2000;
    public static final int AVID_READER_SHARE_BADGE = 2001;
    public static final int AVID_READER_ACHIEVE_DAILY_GOAL = 2006;
    public static final int AVID_READER_START_BOOK_FROM_COLLECTION = 2003;
    public static final int AVID_READER_LIKE_BOOKS = 2004;
    public static final int AVID_READER_READ_ON_2_CONTINUOUS_DAYS = 2005;

    // Voracious Reader
    public static final int VORACIOUS_READER_ACHIEVE_DAILY_GOAL = 3000;
    public static final int VORACIOUS_READER_SHARE_BADGE = 3001;
    public static final int VORACIOUS_READER_CHANGE_READER_BACKGROUND = 3002;
    public static final int VORACIOUS_READER_SHARE_ONE_QUOTE = 3004;
    public static final int VORACIOUS_READER_COMPLETE_BOOKS = 3005;
    public static final int VORACIOUS_READER_READ_ON_3_CONTINUOUS_DAYS = 3006;

    // Critical Reasoner
    public static final int CRITICAL_REASONER_ACHIEVE_DAILY_GOAL = 4000;
    public static final int CRITICAL_REASONER_SHARE_BADGE = 4001;
    public static final int CRITICAL_REASONER_CHANGE_READER_FONT = 4002;
    public static final int CRITICAL_REASONER_READ_THREE_BOOKS_IN_A_COLLECTION = 4003;
    public static final int CRITICAL_REASONER_SHARE_ONE_QUOTE = 4004;
    public static final int CRITICAL_REASONER_COMPLETE_BOOKS = 4005;
    public static final int CRITICAL_REASONER_LIKE_BOOKS = 4006;

    // Potential Master
    public static final int POTENTIAL_MASTER_ACHIEVE_DAILY_GOAL = 5000;
    public static final int POTENTIAL_MASTER_SHARE_BADGE = 5001;
    public static final int POTENTIAL_MASTER_READ_5_CONTINUOUS_DAYS = 5002;
    public static final int POTENTIAL_MASTER_READ_10_CONTINUOUS_DAYS = 5003;
    public static final int POTENTIAL_MASTER_EXPLORE_MORE_BOOKS_IN_CATEGORIES = 5004;
    public static final int POTENTIAL_MASTER_LIKE_BOOKS = 5005;
    public static final int POTENTIAL_MASTER_COMPLETE_BOOKS = 5006;

    // Master
    public static final int MASTER_SHARE_BADGE = 6000;
    public static final int MASTER_READ_ON_X_CONTINUOUS_DAYS = 6001;
    public static final int MASTER_ACHIEVE_DAILY_GOAL = 6002;
    public static final int MASTER_SHARE_ONE_QUOTE = 6003;
    public static final int MASTER_SHARE_FINISHED_BOOK = 6004;
    public static final int MASTER_LIKE_BOOK = 6005;
    public static final int MASTER_COMPLETE_BOOKS = 6006;
  }

  public static class Metadata {

    public static Metadata NONE = new Metadata(-1);

    int value;

    public Metadata(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

}
