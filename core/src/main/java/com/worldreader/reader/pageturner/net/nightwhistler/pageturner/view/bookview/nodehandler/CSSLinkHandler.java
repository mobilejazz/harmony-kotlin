package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.nodehandler;

import android.text.SpannableStringBuilder;
import android.util.Log;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.css.CompiledRule;
import org.htmlcleaner.TagNode;

import java.util.*;

public class CSSLinkHandler extends TagNodeHandler {

  public static final String TAG = CSSLinkHandler.class.getSimpleName();

  private TextLoader textLoader;

  public CSSLinkHandler() {
  }

  public CSSLinkHandler(TextLoader textLoader) {
    this.textLoader = textLoader;
  }

  public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {
    final String type = node.getAttributeByName("type");
    final String href = node.getAttributeByName("href");

    Log.d(TAG, "Found link tag: type=" + type + " and href=" + href);

    if (type == null || !type.equals("text/css")) {
      Log.d(TAG, "Ignoring link of type " + type);
    }

    if (textLoader != null) {
      final List<CompiledRule> rules = textLoader.getCSSRules(href);

      for (CompiledRule rule : rules) {
        spanStack.registerCompiledRule(rule);
      }
    }
  }

  public void setTextLoader(TextLoader textLoader) {
    this.textLoader = textLoader;
  }
}
