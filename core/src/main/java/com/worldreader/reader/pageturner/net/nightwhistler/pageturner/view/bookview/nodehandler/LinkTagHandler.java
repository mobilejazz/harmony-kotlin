/*
 * Copyright (C) 2013 Alex Kuiper
 *
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */
package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import org.htmlcleaner.TagNode;

import java.util.*;

public class LinkTagHandler extends TagNodeHandler {

  private static final List<String> EXTERNAL_PROTOCOLS = new ArrayList<String>() {{
    add("http://");
    add("epub://");
    add("https://");
    add("http://");
    add("ftp://");
    add("mailto:");
  }};

  private LinkTagCallBack callBack;

  public LinkTagHandler() {
  }

  public LinkTagHandler(LinkTagCallBack callBack) {
    this.callBack = callBack;
  }

  @Override public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {
    final String href = node.getAttributeByName("href");
    if (TextUtils.isEmpty(href)) {
      return;
    }

    // First check if it should be a normal URL link
    for (String protocol : EXTERNAL_PROTOCOLS) {
      if (href.toLowerCase(Locale.US).startsWith(protocol)) {
        builder.setSpan(new URLSpan(href), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return;
      }
    }

    // If not, consider it an internal nav link.
    final ClickableSpan span = new ClickableSpan() {
      @Override public void onClick(View widget) {
        if (callBack != null) {
          callBack.onLinkClicked(href);
        }
      }
    };
    spanStack.pushSpan(span, start, end);
  }

  public void setCallBack(final LinkTagCallBack callBack) {
    this.callBack = callBack;
  }

  public interface LinkTagCallBack {

    void onLinkClicked(String href);
  }
}