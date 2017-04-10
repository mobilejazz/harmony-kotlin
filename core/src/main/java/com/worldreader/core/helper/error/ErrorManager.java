package com.worldreader.core.helper.error;

import android.app.Activity;
import com.worldreader.core.common.deprecated.error.ErrorCore;

public interface ErrorManager {

  void perform(Activity activity, ErrorCore errorCore, int screen);
}
