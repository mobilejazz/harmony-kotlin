package com.worldreader.core.domain.model;

import java.io.*;
import java.util.*;

public class Banner implements Serializable {

  public static final String READ_TO_KIDS_BANNER_IDENTIFIER = "r2kmain";

  public static final int OFFLINE_BANNER_ID = -1;

  public static final Banner EMPTY = new Banner();

  private int id;
  private String name;
  private Date start;
  private Date end;
  private String image;
  private String type;

  public Banner() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override public String toString() {
    return "Banner{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", start=" + start +
        ", end=" + end +
        ", image='" + image + '\'' +
        ", type='" + type + '\'' +
        '}';
  }
}
