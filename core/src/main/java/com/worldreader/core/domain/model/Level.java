package com.worldreader.core.domain.model;

import com.worldreader.core.domain.model.user.Milestone;

import java.util.*;

public class Level implements Cloneable {

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

    public static Level.Name getLevelFromScore(int score) {
      Name returnName = null;

      for (Name name : values()) {
        if (score <= name.getRangePoints()) {
          returnName = name;
          break;
        }
      }

      if (returnName == null) {
        returnName = Name.MASTER;
      }

      return returnName;
    }

    public static List<Name> getRemaining(Name name) {
      // If is the last level, we don't return nothing
      if (name.equals(MASTER)) {
        return Collections.emptyList();
      }

      // Otherwise we compute remaining levels from current
      Name[] names = values();

      List<Name> l = new ArrayList<>(names.length);
      l.addAll(Arrays.asList(names).subList(name.getNextLevel().ordinal(), names.length));
      return l;
    }
  }

  private Name name;
  private Set<Milestone> milestones;

  public Level(Name name, Set<Milestone> milestones) {
    this.name = name;
    this.milestones = milestones;
  }

  public Name getName() {
    return name;
  }

  public Set<Milestone> getMilestones() {
    return milestones;
  }

  @Override public Object clone() throws CloneNotSupportedException {
    Level cloned = (Level) super.clone();
    cloned.milestones = new LinkedHashSet<>(cloned.getMilestones());
    return super.clone();
  }
}
