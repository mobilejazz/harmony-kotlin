package com.worldreader.core.application.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.worldreader.core.R;
import com.worldreader.core.userflow.model.TutorialModel;

import java.util.*;

public class TutorialView extends FrameLayout {

  private TextView titleTv;
  private TextView messageTv;
  private TextView progressTv;
  private Button oneBtn;
  private Button twoBtn;

  private ImageView trianguleLeftImg;
  private ImageView trianguleCenterImg;
  private ImageView trianguleRightImg;

  private List<TutorialModel> tutorials;
  private TutorialListener tutorialListener;
  private boolean isTrianglesDisabled = false;
  private int currentTutorialPosition = 0;

  public TutorialView(Context context) {
    super(context);
    init();
  }

  public TutorialView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public TutorialView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public TutorialView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  public void setIsTrianglesDisabled(boolean isTrianglesDisabled) {
    this.isTrianglesDisabled = isTrianglesDisabled;
  }

  public void setTutorials(List<TutorialModel> tutorials) {
    if (tutorials == null) {
      throw new IllegalArgumentException("The tutorials must be not null");
    }

    currentTutorialPosition = 0;

    if (tutorials.size() > 0) {
      this.tutorials = tutorials;

      TutorialModel tutorial = tutorials.get(currentTutorialPosition);
      displayTutorial(tutorial);
      if (!isTrianglesDisabled) {
        displayFirstTriangles();
      }

      oneBtn.setVisibility(View.INVISIBLE);

      if (tutorials.size() > 1) {
        oneBtn.setVisibility(View.VISIBLE);

        if (!isTrianglesDisabled) {
          displaySecondTriangles();
        }
        updateTutorialProgress(1, tutorials.size());
      } else {
        progressTv.setVisibility(View.INVISIBLE);
      }
    } else {
      if (tutorialListener != null) {
        tutorialListener.onCompleted();
      }
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Public methods
  ///////////////////////////////////////////////////////////////////////////

  private void displayTutorial(TutorialModel tutorial) {
    setTitle(tutorial.getTitle());
    setMessage(tutorial.getMessage());
    setButtonOne(tutorial.getButtonOneTitle());
    setButtonTwo(tutorial.getButtonTwoTitle());
  }

  public void updateTutorialProgress(int current, int total) {
    StringBuilder builder = new StringBuilder(3);
    builder.append(current);
    builder.append("/");
    builder.append(total);

    progressTv.setText(builder.toString());
  }

  public void setTutorialListener(TutorialListener tutorialListener) {
    this.tutorialListener = tutorialListener;
  }

  private void setButtonTwo(String buttonTwoTitle) {
    if (!TextUtils.isEmpty(buttonTwoTitle)) {
      twoBtn.setText(buttonTwoTitle);
    } else {
      twoBtn.setVisibility(View.GONE);
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private void setButtonOne(String buttonOneTitle) {
    if (!TextUtils.isEmpty(buttonOneTitle)) {
      oneBtn.setText(buttonOneTitle);
    } else {
      oneBtn.setVisibility(View.GONE);
    }
  }

  private void setMessage(String message) {
    if (!TextUtils.isEmpty(message)) {
      messageTv.setText(message);
    } else {
      messageTv.setVisibility(View.GONE);
    }
  }

  private void setTitle(String title) {
    if (!TextUtils.isEmpty(title)) {
      titleTv.setText(title);
    } else {
      titleTv.setVisibility(View.GONE);
    }
  }

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.global_tutorial_layout, this, true);

    this.titleTv = (TextView) findViewById(R.id.global_tutorial_layout_title);
    this.messageTv = (TextView) findViewById(R.id.global_tutorial_layout_message);
    this.progressTv = (TextView) findViewById(R.id.global_tutorial_layout_progress_tv);
    this.oneBtn = (Button) findViewById(R.id.global_tutorial_layout_button_one);
    this.twoBtn = (Button) findViewById(R.id.global_tutorial_layout_button_two);

    this.trianguleLeftImg = (ImageView) findViewById(R.id.global_tutorial_layout_triangule_left_img);
    this.trianguleCenterImg = (ImageView) findViewById(R.id.global_tutorial_layout_triangule_center_img);
    this.trianguleRightImg = (ImageView) findViewById(R.id.global_tutorial_layout_triangule_right_img);

    oneBtn.setOnClickListener(new OnClickListener() {
      @Override public void onClick(final View v) {
        if (tutorialListener != null) {
          tutorialListener.onCompleted();
        }
      }
    });

    twoBtn.setOnClickListener(new OnClickListener() {
      @Override public void onClick(final View v) {
        currentTutorialPosition++;

        if (currentTutorialPosition >= tutorials.size()) {
          if (tutorialListener != null) {
            tutorialListener.onCompleted();
          }
        } else {
          TutorialModel tutorial = tutorials.get(currentTutorialPosition);
          displayTutorial(tutorial);
          int viewProgress = currentTutorialPosition + 1;
          updateTutorialProgress(viewProgress, tutorials.size());

          if (!isTrianglesDisabled) {
            displayThirdTriangles();
          }
        }
      }
    });

    hideTriangles();
  }

  private void setTriangleLeftVisibility(int visibility) {
    trianguleLeftImg.setVisibility(visibility);
  }

  private void setTriangleCenterVisibility(int visibility) {
    trianguleCenterImg.setVisibility(visibility);
  }

  private void setTriangleRightVisibility(int visibility) {
    trianguleRightImg.setVisibility(visibility);
  }

  private void hideTriangles() {
    setTriangleLeftVisibility(View.INVISIBLE);
    setTriangleCenterVisibility(View.INVISIBLE);
    setTriangleRightVisibility(View.INVISIBLE);
  }

  private void displayFirstTriangles() {
    setTriangleLeftVisibility(View.VISIBLE);
    setTriangleCenterVisibility(View.INVISIBLE);
    setTriangleRightVisibility(View.INVISIBLE);
  }

  private void displaySecondTriangles() {
    setTriangleLeftVisibility(View.INVISIBLE);
    setTriangleCenterVisibility(View.VISIBLE);
    setTriangleRightVisibility(View.INVISIBLE);
  }

  private void displayThirdTriangles() {
    setTriangleLeftVisibility(View.INVISIBLE);
    setTriangleCenterVisibility(View.INVISIBLE);
    setTriangleRightVisibility(View.VISIBLE);
  }

  public interface TutorialListener {

    void onCompleted();
  }

  public void release() {
    this.tutorialListener = null;
  }
}
