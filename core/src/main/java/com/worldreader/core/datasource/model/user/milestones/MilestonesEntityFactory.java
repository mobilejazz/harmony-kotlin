package com.worldreader.core.datasource.model.user.milestones;

import com.worldreader.core.datasource.model.user.LevelEntity.Name;

import java.util.*;

import static com.worldreader.core.datasource.model.user.milestones.MilestoneEntity.ID;
import static com.worldreader.core.datasource.model.user.milestones.MilestoneEntity.State;

public class MilestonesEntityFactory {

  private MilestonesEntityFactory() {
    throw new AssertionError("No instances of this class are allowed!");
  }

  public static Set<MilestoneEntity> createMilestone(Name name) {
    switch (name) {
      case VISITOR:
        return createVisitorMilestones();
      case AVID_READER:
        return createAvidReaderMilestones();
      case VORACIOUS_READER:
        return createVoraciousReaderMilestones();
      case CRITICAL_REASONER:
        return createCriticalReasonerMilestones();
      case POTENTIAL_MASTER:
        return createPotentialMasterMilestones();
      case MASTER:
        return createMasterMilestones();
    }

    throw new IllegalArgumentException("Invalid Level.Name passed: " + name);
  }

  public static Set<MilestoneEntity> createAllMilestones() {
    final Set<MilestoneEntity> milestones = new LinkedHashSet<>();
    milestones.addAll(createVisitorMilestones());
    milestones.addAll(createAvidReaderMilestones());
    milestones.addAll(createVoraciousReaderMilestones());
    milestones.addAll(createCriticalReasonerMilestones());
    milestones.addAll(createPotentialMasterMilestones());
    milestones.addAll(createMasterMilestones());
    return milestones;
  }

  //region Private Methods

  private static Set<MilestoneEntity> createVisitorMilestones() {
    Set<MilestoneEntity> set = new LinkedHashSet<>();
    set.add(milestone(ID.VISITOR_LIKE_BOOK, "Like one book", 5, State.PENDING, metadata(1)));
    set.add(milestone(ID.VISITOR_SET_YOUR_GOALS, "Set your goals", 3, State.PENDING,
        MilestoneEntity.Metadata.NONE));
    set.add(
        milestone(ID.VISITOR_ACHIEVE_DAILY_GOAL, "Login with Facebook or Google", 8, State.PENDING,
            metadata(1)));
    set.add(milestone(ID.VISITOR_READ_ON_2_CONTINUOUS_DAYS, "Login with Facebook or Google", 3,
        State.PENDING, metadata(2)));
    set.add(milestone(ID.VISITOR_READ_ON_3_CONTINUOUS_DAYS, "Login with Facebook or Google", 5,
        State.PENDING, metadata(3)));
    set.add(milestone(ID.VISITOR_FULFILL_PROFILE_INFORMATION, "Login with Facebook or Google", 5,
        State.PENDING, MilestoneEntity.Metadata.NONE));
    return set;
  }

  private static Set<MilestoneEntity> createAvidReaderMilestones() {
    Set<MilestoneEntity> set = new LinkedHashSet<>();
    set.add(milestone(ID.AVID_READER_USE_TTS, "Use the read aloud feature while reading", 10,
        State.PENDING, MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.AVID_READER_SHARE_BADGE, "Share your new badge on social media", 10,
        State.PENDING, MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.AVID_READER_ACHIEVE_DAILY_GOAL, "Achieve daily goal", 10, State.PENDING,
        metadata(2)));
    set.add(
        milestone(ID.AVID_READER_START_BOOK_FROM_COLLECTION, "Start a book from a collection", 10,
            State.PENDING, MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.AVID_READER_LIKE_BOOKS, "Like 5 books", 10, State.PENDING, metadata(5)));
    set.add(milestone(ID.AVID_READER_READ_ON_2_CONTINUOUS_DAYS, "Read from two continuous days", 5,
        State.PENDING, metadata(2)));
    return set;
  }

  private static Set<MilestoneEntity> createVoraciousReaderMilestones() {
    Set<MilestoneEntity> set = new LinkedHashSet<>();
    set.add(milestone(ID.VORACIOUS_READER_ACHIEVE_DAILY_GOAL, "Achieve daily goal 3 times", 10,
        State.PENDING, metadata(5)));
    set.add(milestone(ID.VORACIOUS_READER_SHARE_BADGE, "Share your new badge on social media", 15,
        State.PENDING, MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.VORACIOUS_READER_CHANGE_READER_BACKGROUND,
        "Change the background of your reader", 10, State.PENDING, MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.VORACIOUS_READER_SHARE_ONE_QUOTE, "Share one quote", 20, State.PENDING,
        MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.VORACIOUS_READER_COMPLETE_BOOKS, "Finish one book", 50, State.PENDING,
        metadata(3)));
    set.add(
        milestone(ID.VORACIOUS_READER_READ_ON_3_CONTINUOUS_DAYS, "Read for 3 continuous days", 10,
            State.PENDING, metadata(3)));
    return set;
  }

  private static Set<MilestoneEntity> createCriticalReasonerMilestones() {
    Set<MilestoneEntity> set = new LinkedHashSet<>();
    set.add(
        milestone(ID.CRITICAL_REASONER_ACHIEVE_DAILY_GOAL, "Achieve your daily goal 5 times", 20,
            State.PENDING, metadata(8)));
    set.add(milestone(ID.CRITICAL_REASONER_SHARE_BADGE, "Share your new badge on social media", 20,
        State.PENDING, MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.CRITICAL_REASONER_CHANGE_READER_FONT, "Change your reading font", 20,
        State.PENDING, MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.CRITICAL_REASONER_READ_THREE_BOOKS_IN_A_COLLECTION,
        "Read 3 books in one collection", 150, State.PENDING, metadata(3)));
    set.add(milestone(ID.CRITICAL_REASONER_SHARE_ONE_QUOTE, "Share one quote", 30, State.PENDING,
        MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.CRITICAL_REASONER_COMPLETE_BOOKS, "Complete five books", 50, State.PENDING,
        metadata(5)));
    set.add(milestone(ID.CRITICAL_REASONER_LIKE_BOOKS, "Like 15 books", 30, State.PENDING,
        metadata(15)));
    return set;
  }

  private static Set<MilestoneEntity> createPotentialMasterMilestones() {
    Set<MilestoneEntity> set = new LinkedHashSet<>();
    set.add(
        milestone(ID.POTENTIAL_MASTER_ACHIEVE_DAILY_GOAL, "Achieve your daily goal 15 times", 50,
            State.PENDING, metadata(15)));
    set.add(milestone(ID.POTENTIAL_MASTER_SHARE_BADGE, "Share your new badge on social media", 30,
        State.PENDING, MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.POTENTIAL_MASTER_READ_5_CONTINUOUS_DAYS, "Read for 5 continuous days", 80,
        State.PENDING, metadata(5)));
    set.add(
        milestone(ID.POTENTIAL_MASTER_READ_10_CONTINUOUS_DAYS, "Read for 10 continuous days", 200,
            State.PENDING, metadata(10)));
    set.add(milestone(ID.POTENTIAL_MASTER_EXPLORE_MORE_BOOKS_IN_CATEGORIES,
        "Explore 'More Books' in Categories", 50, State.PENDING, MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.POTENTIAL_MASTER_LIKE_BOOKS, "Like 10 books", 100, State.PENDING,
        metadata(30)));
    set.add(milestone(ID.POTENTIAL_MASTER_COMPLETE_BOOKS, "Complete 10 books", 300, State.PENDING,
        metadata(10)));
    return set;
  }

  private static Set<MilestoneEntity> createMasterMilestones() {
    Set<MilestoneEntity> set = new LinkedHashSet<>();
    set.add(
        milestone(ID.MASTER_SHARE_BADGE, "Share your new badge on social media", 15, State.PENDING,
            MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.MASTER_READ_ON_X_CONTINUOUS_DAYS, "Read on 20 continuous days", 0,
        State.PENDING, metadata(20)));
    set.add(milestone(ID.MASTER_ACHIEVE_DAILY_GOAL, "Achieve daily goals x times", 0, State.PENDING,
        metadata(20)));
    set.add(milestone(ID.MASTER_SHARE_ONE_QUOTE, "Share one quote", 20, State.PENDING,
        MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.MASTER_SHARE_FINISHED_BOOK, "Share a finished book", 50, State.PENDING,
        MilestoneEntity.Metadata.NONE));
    set.add(milestone(ID.MASTER_LIKE_BOOK, "Like 50 book", 10, State.PENDING, metadata(50)));
    set.add(
        milestone(ID.MASTER_COMPLETE_BOOKS, "Complete 20 books", 800, State.PENDING, metadata(20)));
    return set;
  }

  private static MilestoneEntity.Metadata metadata(int metadata) {
    return new MilestoneEntity.Metadata(metadata);
  }

  private static MilestoneEntity milestone(int id, String description, int points, State state,
      MilestoneEntity.Metadata metadata) {
    return new MilestoneEntity(id, description, points, state, metadata);
  }

  //endregion
}
