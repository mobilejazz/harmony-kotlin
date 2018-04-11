package net.nightwhistler.htmlspanner.spans;

import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;

public class AlignNormalSpan implements AlignmentSpan {

  @Override
  public Alignment getAlignment() {
    return Alignment.ALIGN_NORMAL;
  }
}
