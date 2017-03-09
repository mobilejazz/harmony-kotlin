package com.worldreader.core.datasource.network.model;

import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import com.worldreader.core.datasource.model.user.milestones.MilestoneEntity;
import com.worldreader.core.datasource.model.user.milestones.UserMilestoneEntity;

import java.util.*;

public class MilestonesNetworkBody {

  @SerializedName("milestones") private List<MilestoneBody> levels;

  public static MilestonesNetworkBody of(List<UserMilestoneEntity> milestones) {
    final List<MilestoneBody> levels = Lists.newArrayList();
    for (final UserMilestoneEntity milestone : milestones) {
      levels.add(
          new MilestoneBody(Integer.valueOf(milestone.getMilestoneId()), milestone.getScore()));
    }
    final MilestonesNetworkBody networkBody = new MilestonesNetworkBody();
    networkBody.setMilestones(levels);

    return networkBody;
  }

  public MilestonesNetworkBody() {
  }

  public MilestonesNetworkBody(MilestoneEntity milestoneEntity) {
    this(Collections.singletonList(milestoneEntity));
  }

  public MilestonesNetworkBody(List<MilestoneEntity> milestones) {
    this.levels = new ArrayList<>();

    for (MilestoneEntity milestone : milestones) {
      this.levels.add(new MilestoneBody(milestone.getId(), milestone.getPoints()));
    }
  }

  public List<MilestoneBody> getMilestones() {
    return levels;
  }

  public void setMilestones(List<MilestoneBody> levels) {
    this.levels = levels;
  }

  public static class MilestoneBody {

    @SerializedName("id") int id;
    @SerializedName("points") int points;

    public MilestoneBody(int id, int points) {
      this.id = id;
      this.points = points;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public int getPoints() {
      return points;
    }

    public void setPoints(int points) {
      this.points = points;
    }
  }
}
