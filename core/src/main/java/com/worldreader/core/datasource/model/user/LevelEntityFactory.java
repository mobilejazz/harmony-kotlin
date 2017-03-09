package com.worldreader.core.datasource.model.user;

import com.worldreader.core.datasource.model.user.milestones.MilestonesEntityFactory;

import java.util.*;

import static com.worldreader.core.datasource.model.user.LevelEntity.Name;

public class LevelEntityFactory {

  private LevelEntityFactory() {
    throw new AssertionError("No instances of this class are allowed!");
  }

  public static LevelEntity createFrom(LevelEntity.Name level) {
    switch (level) {
      case VISITOR:
        return createVisitorLevel();
      case AVID_READER:
        return createAvidReaderLevel();
      case VORACIOUS_READER:
        return createVoraciousReaderLevel();
      case CRITICAL_REASONER:
        return createCriticalReasonerLevel();
      case POTENTIAL_MASTER:
        return createPotentialMasterLevel();
      case MASTER:
        return createMasterLevel();
    }
    throw new IllegalArgumentException("Current level is invalid: " + level);
  }

  public static Set<LevelEntity> createToLevel(LevelEntity.Name level) {
    int topLevelToReach = level.ordinal();
    LevelEntity.Name[] values = LevelEntity.Name.values();

    Set<LevelEntity> levels = new LinkedHashSet<>();
    for (int i = 0; i <= topLevelToReach; i++) {
      LevelEntity.Name currentLevel = values[i];
      levels.add(createFrom(currentLevel));
    }

    return levels;
  }

  public static LevelEntity createVisitorLevel() {
    return new LevelEntity(Name.VISITOR, MilestonesEntityFactory.createMilestone(Name.VISITOR));
  }

  public static LevelEntity createAvidReaderLevel() {
    return new LevelEntity(Name.AVID_READER,
        MilestonesEntityFactory.createMilestone(Name.AVID_READER));
  }

  public static LevelEntity createVoraciousReaderLevel() {
    return new LevelEntity(Name.VORACIOUS_READER,
        MilestonesEntityFactory.createMilestone(Name.VORACIOUS_READER));
  }

  public static LevelEntity createCriticalReasonerLevel() {
    return new LevelEntity(Name.CRITICAL_REASONER,
        MilestonesEntityFactory.createMilestone(Name.CRITICAL_REASONER));
  }

  public static LevelEntity createPotentialMasterLevel() {
    return new LevelEntity(Name.POTENTIAL_MASTER,
        MilestonesEntityFactory.createMilestone(Name.POTENTIAL_MASTER));
  }

  public static LevelEntity createMasterLevel() {
    return new LevelEntity(Name.MASTER,
        MilestonesEntityFactory.createMilestone(Name.POTENTIAL_MASTER));
  }
}
