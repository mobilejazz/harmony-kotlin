package com.worldreader.core.userflow.model;

import android.content.Context;
import android.content.res.Resources;
import com.worldreader.core.R;

public class TutorialModel {

  private String title;
  private String message;
  private String buttonOneTitle;
  private String buttonTwoTitle;
  private Type type;

  private TutorialModel(Type type, String title, String message, String buttonOneTitle,
      String buttonTwoTitle) {
    this.type = type;
    this.title = title;
    this.message = message;
    this.buttonOneTitle = buttonOneTitle;
    this.buttonTwoTitle = buttonTwoTitle;
  }

  private static TutorialModel create(Type type, String title, String message,
      String buttonOneTitle, String buttonTwoTitle) {
    return new TutorialModel(type, title, message, buttonOneTitle, buttonTwoTitle);
  }

  public static TutorialModel createMyLibraryTutorial(Context context) {
    if (context != null) {
      Resources res = context.getResources();
      return create(Type.TUTORIAL, res.getString(R.string.ls_my_library_tutorial_title),
          res.getString(R.string.ls_my_library_tutorial_message),
          res.getString(R.string.ls_my_library_tutorial_button_one),
          res.getString(R.string.ls_categories_tutorial_button_two));
    } else {
      return null;
    }
  }

  public static TutorialModel createCollectionTutorial(Context context) {
    if (context != null) {
      Resources res = context.getResources();
      return create(Type.TUTORIAL, null, res.getString(R.string.ls_collections_tutorial_message),
          res.getString(R.string.ls_my_library_tutorial_button_one),
          res.getString(R.string.ls_my_library_tutorial_button_two));
    } else {
      return null;
    }
  }

  public static TutorialModel createCategoriesTutorial(Context context) {
    if (context != null) {
      Resources res = context.getResources();
      return create(Type.TUTORIAL, null, res.getString(R.string.ls_categories_tutorial_message),
          res.getString(R.string.ls_my_library_tutorial_button_one),
          res.getString(R.string.ls_categories_tutorial_button_two));
    } else {
      return null;
    }
  }

  public static TutorialModel createInitialReaderTutorial(Context context) {
    if (context != null) {
      Resources res = context.getResources();
      return create(Type.TUTORIAL, res.getString(R.string.ls_tutorial_reader_initial_title),
          res.getString(R.string.ls_tutorial_reader_initial_message),
          res.getString(R.string.ls_my_library_tutorial_button_one),
          res.getString(R.string.ls_categories_tutorial_button_two));
    } else {
      return null;
    }
  }

  public static TutorialModel createOptionsReaderTutorial(Context context) {
    if (context != null) {
      Resources res = context.getResources();
      return create(Type.TUTORIAL, res.getString(R.string.ls_tutorial_reader_options_title),
          res.getString(R.string.ls_tutorial_reader_options_message),
          res.getString(R.string.ls_my_library_tutorial_button_one),
          res.getString(R.string.ls_my_library_tutorial_button_two));
    } else {
      return null;
    }
  }

  public static TutorialModel createIndexReaderTutorial(Context context) {
    if (context != null) {
      Resources res = context.getResources();
      return create(Type.TUTORIAL, res.getString(R.string.ls_tutorial_reader_index_title),
          res.getString(R.string.ls_tutorial_reader_index_message),
          res.getString(R.string.ls_my_library_tutorial_button_one),
          res.getString(R.string.ls_categories_tutorial_button_two));
    } else {
      return null;
    }
  }

  public static TutorialModel createSetGoalsTutorial() {
    return create(Type.SET_YOUR_GOALS, null, null, null, null);
  }

  public static TutorialModel createBecomeWorldreaderTutorial() {
    return create(Type.BECOME_WORLDREADER, null, null, null, null);
  }

  public String getTitle() {
    return title;
  }

  public String getMessage() {
    return message;
  }

  public String getButtonOneTitle() {
    return buttonOneTitle;
  }

  public String getButtonTwoTitle() {
    return buttonTwoTitle;
  }

  public Type getType() {
    return type;
  }

  public enum Type {
    TUTORIAL,
    SET_YOUR_GOALS,
    BECOME_WORLDREADER
  }
}
