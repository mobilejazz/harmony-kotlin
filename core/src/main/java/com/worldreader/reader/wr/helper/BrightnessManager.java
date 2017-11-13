package com.worldreader.reader.wr.helper;

import android.view.Window;
import android.view.WindowManager;

public class BrightnessManager {

  public BrightnessManager() {
  }

  public void setBrightness(Window window, int level) {
    if (window != null) {
      WindowManager.LayoutParams lp = window.getAttributes();
      lp.screenBrightness = (float) level / 100f;
      window.setAttributes(lp);
    }
  }

}
