package com.worldreader.core.datasource.model.user;

import com.worldreader.core.datasource.model.user.milestones.MilestoneEntity;

import java.util.*;

public class LevelEntity implements Cloneable {

  public enum Name {
    VISITOR(50) {
      @Override public Name getNextLevel() {
        return Name.AVID_READER;
      }
    }, AVID_READER(150) {
      @Override public Name getNextLevel() {
        return VORACIOUS_READER;
      }
    }, VORACIOUS_READER(400) {
      @Override public Name getNextLevel() {
        return CRITICAL_REASONER;
      }
    }, CRITICAL_REASONER(800) {
      @Override public Name getNextLevel() {
        return POTENTIAL_MASTER;
      }
    }, POTENTIAL_MASTER(2000) {
      @Override public Name getNextLevel() {
        return MASTER;
      }
    }, MASTER(4000) {
      @Override public Name getNextLevel() {
        return MASTER;
      }
    };

    private int rangePoints;

    Name(int rangePoints) {
      this.rangePoints = rangePoints;
    }

    public int getRangePoints() {
      return rangePoints;
    }

    public abstract Name getNextLevel();

    public static LevelEntity.Name getLevelFromScore(int score) {
      Name returnName = null;

      for (Name name : values()) {
        if (score <= name.getRangePoints()) {
          returnName = name;
          break;
        } else if (score >= MASTER.getRangePoints()) {
          returnName = MASTER;
          break;
        }
      }

      return returnName;
    }
  }

  private Name name;
  private Set<MilestoneEntity> milestones;

  public LevelEntity(Name name, Set<MilestoneEntity> milestones) {
    this.name = name;
    this.milestones = milestones;
  }

  public Name getName() {
    return name;
  }

  public Set<MilestoneEntity> getMilestones() {
    return milestones;
  }

  @Override public Object clone() throws CloneNotSupportedException {
    LevelEntity cloned = (LevelEntity) super.clone();

    Set<MilestoneEntity> milestones = cloned.getMilestones();

    cloned.milestones = new LinkedHashSet<>(milestones.size());

    for (MilestoneEntity milestoneEntity : milestones) {
      cloned.milestones.add((MilestoneEntity) milestoneEntity.clone());
    }

    return cloned;
  }
}
