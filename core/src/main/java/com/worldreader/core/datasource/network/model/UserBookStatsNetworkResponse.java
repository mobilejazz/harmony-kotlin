package com.worldreader.core.datasource.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.*;

public class UserBookStatsNetworkResponse {

  @SerializedName("totalPoints") private int totalPoints;
  @SerializedName("pointsToday") private int pointsToday;
  @SerializedName("dailyGoal") private int dailyGoal;
  @SerializedName("dailyGoalReached") private boolean dailyGoalReached;
  @SerializedName("dailyGoalReachedCount") private int dailyGoalReachedCount;
  @SerializedName("milestones") private List<Integer> milestones;

  public UserBookStatsNetworkResponse() {
  }

  public int getTotalPoints() {
    return totalPoints;
  }

  public void setTotalPoints(int totalPoints) {
    this.totalPoints = totalPoints;
  }

  public int getPointsToday() {
    return pointsToday;
  }

  public void setPointsToday(int pointsToday) {
    this.pointsToday = pointsToday;
  }

  public int getDailyGoal() {
    return dailyGoal;
  }

  public void setDailyGoal(int dailyGoal) {
    this.dailyGoal = dailyGoal;
  }

  public boolean isDailyGoalReached() {
    return dailyGoalReached;
  }

  public void setDailyGoalReached(boolean dailyGoalReached) {
    this.dailyGoalReached = dailyGoalReached;
  }

  public int getDailyGoalReachedCount() {
    return dailyGoalReachedCount;
  }

  public void setDailyGoalReachedCount(int dailyGoalReachedCount) {
    this.dailyGoalReachedCount = dailyGoalReachedCount;
  }

  public List<Integer> getMilestones() {
    return milestones;
  }

  public void setMilestones(List<Integer> milestones) {
    this.milestones = milestones;
  }
}
