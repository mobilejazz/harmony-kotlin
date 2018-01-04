package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;

// Class that displays properly the text (and media) in books
public class BookTextView extends android.support.v7.widget.AppCompatTextView {

  private static final String TAG = BookTextView.class.getSimpleName();

  private BookView bookView;

  private long blockUntil = 0L;

  public BookTextView(Context context, AttributeSet attributes) {
    super(context, attributes);
    setLayerType(View.LAYER_TYPE_SOFTWARE, null);
  }

  public void setBookView(BookView bookView) {
    this.bookView = bookView;
  }

  public void setBlockUntil(long blockUntil) {
    this.blockUntil = blockUntil;
  }

  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    bookView.onInnerViewResize();
  }

  @Override public boolean dispatchTouchEvent(MotionEvent event) {
    // Workaround to https://code.google.com/p/android/issues/detail?id=191430
    if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
      int startSelection = getSelectionStart();
      int endSelection = getSelectionEnd();
      if (startSelection != endSelection) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
          final CharSequence text = getText();
          setText(null);
          setText(text);
        }
      }
    }

    return super.dispatchTouchEvent(event);
  }

  @Override public void onWindowFocusChanged(boolean hasWindowFocus) {
    //We override this method to do nothing, since the base implementation closes the ActionMode.
    //This means that when the user clicks the overflow menu, the ActionMode is stopped and text selection is ended.
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override public ActionMode startActionMode(ActionMode.Callback callback) {
    final long now = System.currentTimeMillis();
    if (now > blockUntil) {
      Log.d(TAG, "InnerView starting action-mode");
      return super.startActionMode(callback);
    } else {
      Log.d(TAG, "Not starting action-mode yet, since block time hasn't expired.");
      clearFocus();
      return null;
    }
  }
}
