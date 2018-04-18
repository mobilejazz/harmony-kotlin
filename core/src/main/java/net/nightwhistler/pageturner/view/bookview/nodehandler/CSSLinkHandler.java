package net.nightwhistler.pageturner.view.bookview.nodehandler;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.css.CSSCompiledRule;
import net.nightwhistler.htmlspanner.handlers.TagNodeHandler;
import net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
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

    if (TextUtils.isEmpty(type) || !type.equals("text/css")) {
      Log.d(TAG, "Ignoring link of type " + type);
      return;
    }

    if (textLoader != null) {
      final List<CSSCompiledRule> rules = textLoader.getCSSRules(href);
      for (CSSCompiledRule rule : rules) {
        spanStack.registerCompiledRule(rule);
      }
    }
  }

  public void setTextLoader(TextLoader textLoader) {
    this.textLoader = textLoader;
  }
}
