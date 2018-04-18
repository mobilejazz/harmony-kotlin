package net.nightwhistler.htmlspanner;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import net.nightwhistler.htmlspanner.css.CSSCompiledRule;
import net.nightwhistler.htmlspanner.style.Style;
import org.htmlcleaner.TagNode;

import java.util.*;

/**
 * Simple stack structure that Spans can be pushed on.
 *
 * Handles the lookup and application of CSS styles.
 */
public class SpanStack {

  private static final String TAG = SpanStack.class.getSimpleName();

  private final Set<CSSCompiledRule> rules;
  private final Stack<SpanCallback> spanItemStack;
  private final Map<TagNode, List<CSSCompiledRule>> lookupCache;

  public SpanStack() {
    rules = new HashSet<>();
    spanItemStack = new Stack<>();
    lookupCache = new HashMap<>();
  }

  public void registerCompiledRule(CSSCompiledRule rule) {
    rules.add(rule);
  }

  public Style getStyle(TagNode node, Style baseStyle) {
    if (!lookupCache.containsKey(node)) {
      final String name = node.getName();
      final String id = safeString(node.getAttributeByName("id"));
      final String aClass = safeString(node.getAttributeByName("class"));
      Log.v(TAG, "Looking for matching CSS rules for node: " + "<" + name + " id='" + id + "' class='" + aClass + "'>");

      final List<CSSCompiledRule> matchingRules = new ArrayList<>();
      for (CSSCompiledRule rule : rules) {
        if (rule.matches(node)) {
          matchingRules.add(rule);
        }
      }

      Log.v(TAG, "Found " + matchingRules.size() + " matching rules.");
      lookupCache.put(node, matchingRules);
    }

    Style result = baseStyle;
    for (CSSCompiledRule rule : lookupCache.get(node)) {
      Log.v(TAG, "Applying rule " + rule);

      Style original = result;
      result = rule.applyStyle(result);

      Log.v(TAG, "Original style: " + original);
      Log.v(TAG, "Resulting style: " + result);
    }

    return result;
  }

  private String safeString(String s) {
    return TextUtils.isEmpty(s) ? "" : s;
  }

  public void pushSpan(final Object span, final int start, final int end) {
    if (end > start) {
      final SpanCallback callback = new SpanCallback() {
        @Override
        public void applySpan(HtmlSpanner spanner, SpannableStringBuilder builder) {
          builder.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
      };
      spanItemStack.push(callback);
      return;
    }

    Log.d(TAG, "refusing to put span of type " + span.getClass().getSimpleName() + " and length " + (end - start));
  }

  public void pushSpan(SpanCallback callback) {
    spanItemStack.push(callback);
  }

  public void applySpans(HtmlSpanner spanner, SpannableStringBuilder builder) {
    while (!spanItemStack.isEmpty()) {
      spanItemStack.pop().applySpan(spanner, builder);
    }
  }

}
