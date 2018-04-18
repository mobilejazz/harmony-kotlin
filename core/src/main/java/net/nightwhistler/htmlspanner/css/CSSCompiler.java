package net.nightwhistler.htmlspanner.css;

import android.graphics.Color;
import android.util.Log;
import com.osbcp.cssparser.PropertyValue;
import com.osbcp.cssparser.Rule;
import com.osbcp.cssparser.Selector;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.HtmlSpanner;
import net.nightwhistler.htmlspanner.style.Style;
import net.nightwhistler.htmlspanner.style.StyleValue;
import org.htmlcleaner.TagNode;

import java.util.*;

/**
 * Compiler for CSS Rules.
 *
 * The compiler takes the raw parsed form (a Rule) of a CSS rule
 * and transforms it into an executable CSSCompiledRule where all
 * the parsing of values has already been done.
 */
public class CSSCompiler {

  private static final String TAG = CSSCompiler.class.getSimpleName();

  public static CSSCompiledRule compile(Rule rule, HtmlSpanner spanner) {
    Log.d(TAG, "Compiling rule " + rule);

    final List<List<TagNodeMatcher>> matchers = new ArrayList<>();
    final List<StyleUpdater> styleUpdaters = new ArrayList<>();

    for (Selector selector : rule.getSelectors()) {
      List<TagNodeMatcher> selMatchers = CSSCompiler.createMatchersFromSelector(selector);
      matchers.add(selMatchers);
    }

    Style blank = new Style();

    for (PropertyValue propertyValue : rule.getPropertyValues()) {
      final StyleUpdater updater = CSSCompiler.getStyleUpdater(propertyValue.getProperty(), propertyValue.getValue());
      if (updater != null) {
        styleUpdaters.add(updater);
        blank = updater.updateStyle(blank, spanner);
      }
    }

    Log.d(TAG, "Compiled rule: " + blank);

    final String asText = rule.toString();

    return new CSSCompiledRule(spanner, matchers, styleUpdaters, asText);
  }

  private static Integer parseCSSColor(String colorString) {
    //Check for CSS short-hand notation: #0fc -> #00ffcc
    if (colorString.length() == 4 && colorString.startsWith("#")) {
      final StringBuilder builder = new StringBuilder("#");
      for (int i = 1; i < colorString.length(); i++) {
        //Duplicate each char
        builder.append(colorString.charAt(i));
        builder.append(colorString.charAt(i));
      }

      colorString = builder.toString();
    }

    return Color.parseColor(colorString);
  }

  private static List<TagNodeMatcher> createMatchersFromSelector(Selector selector) {
    final List<TagNodeMatcher> matchers = new ArrayList<>();
    final String selectorString = selector.toString();
    final String[] parts = selectorString.split("\\s");

    //Create a reversed matcher list
    for (int i = parts.length - 1; i >= 0; i--) {
      matchers.add(createMatcherFromPart(parts[i]));
    }

    return matchers;
  }

  private static TagNodeMatcher createMatcherFromPart(String selectorPart) {
    //Match by class
    if (selectorPart.indexOf('.') != -1) {
      return new ClassMatcher(selectorPart);
    }

    if (selectorPart.startsWith("#")) {
      return new IdMatcher(selectorPart);
    }

    return new TagNameMatcher(selectorPart);
  }

  private static class ClassMatcher implements TagNodeMatcher {

    private String tagName;
    private String className;

    private ClassMatcher(String selectorString) {
      String[] elements = selectorString.split("\\.");

      if (elements.length == 2) {
        tagName = elements[0];
        className = elements[1];
      }
    }

    @Override
    public boolean matches(TagNode tagNode) {
      if (tagNode == null) {
        return false;
      }

      //If a tag name is given it should match
      if (tagName != null && tagName.length() > 0 && !tagName.equals(tagNode.getName())) {
        return false;
      }

      String classAttribute = tagNode.getAttributeByName("class");
      return classAttribute != null && classAttribute.equals(className);
    }
  }

  public static StyleUpdater getStyleUpdater(final String key, final String value) {
    if ("color".equals(key)) {
      try {
        final Integer color = parseCSSColor(value);
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            Log.d(TAG, "Applying style " + key + ": " + value);
            return style.setColor(color);
          }
        };
      } catch (IllegalArgumentException ia) {
        Log.e(TAG, "Can't parse colour definition: " + value);
        return null;
      }
    }

    if ("background-color".equals(key)) {
      try {
        final Integer color = parseCSSColor(value);
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            Log.d(TAG, "Applying style " + key + ": " + value);
            return style.setBackgroundColor(color);
          }
        };
      } catch (IllegalArgumentException ia) {
        Log.e(TAG, "Can't parse colour definition: " + value);
        return null;
      }
    }

    if ("align".equals(key) || "text-align".equals(key)) {
      try {
        final Style.TextAlignment alignment = Style.TextAlignment.valueOf(value.toUpperCase());
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            Log.d(TAG, "Applying style " + key + ": " + value);
            return style.setTextAlignment(alignment);
          }
        };

      } catch (IllegalArgumentException i) {
        Log.e(TAG, "Can't parse alignment: " + value);
        return null;
      }
    }

    if ("font-weight".equals(key)) {

      try {
        final Style.FontWeight weight = Style.FontWeight.valueOf(value.toUpperCase());

        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            Log.d(TAG, "Applying style " + key + ": " + value);
            return style.setFontWeight(weight);
          }
        };

      } catch (IllegalArgumentException i) {
        Log.e(TAG, "Can't parse font-weight: " + value);
        return null;
      }
    }

    if ("font-style".equals(key)) {
      try {
        final Style.FontStyle fontStyle = Style.FontStyle.valueOf(value.toUpperCase());
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            Log.d(TAG, "Applying style " + key + ": " + value);
            return style.setFontStyle(fontStyle);
          }
        };
      } catch (IllegalArgumentException i) {
        Log.e(TAG, "Can't parse font-style: " + value);
        return null;
      }
    }

    if ("font-family".equals(key)) {
      return new StyleUpdater() {
        @Override
        public Style updateStyle(Style style, HtmlSpanner spanner) {
          Log.d(TAG, "Applying style " + key + ": " + value);

          FontFamily family = spanner.getFont(value);

          Log.d(TAG, "Got font " + family);

          return style.setFontFamily(family);
        }
      };

    }

    if ("font-size".equals(key)) {

      final StyleValue styleValue = StyleValue.parse(value);

      if (styleValue != null) {

        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            Log.d(TAG, "Applying style " + key + ": " + value);
            return style.setFontSize(styleValue);
          }
        };

      } else {

        //Fonts have an extra legacy format where you just specify a plain number.
        try {
          final Float number = translateFontSize(Integer.parseInt(value));
          return new StyleUpdater() {
            @Override
            public Style updateStyle(Style style, HtmlSpanner spanner) {
              Log.d(TAG, "Applying style " + key + ": " + value);
              return style.setFontSize(new StyleValue(number, StyleValue.Unit.EM));
            }
          };
        } catch (NumberFormatException nfe) {
          Log.e(TAG, "Can't parse font-size: " + value);
          return null;
        }
      }
    }

    if ("margin-bottom".equals(key)) {
      final StyleValue styleValue = StyleValue.parse(value);

      if (styleValue != null) {
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            return style.setMarginBottom(styleValue);
          }
        };
      }
    }

    if ("margin-top".equals(key)) {

      final StyleValue styleValue = StyleValue.parse(value);

      if (styleValue != null) {
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            return style.setMarginTop(styleValue);
          }
        };
      }
    }

    if ("margin-left".equals(key)) {

      final StyleValue styleValue = StyleValue.parse(value);

      if (styleValue != null) {
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            return style.setMarginLeft(styleValue);
          }
        };
      }
    }

    if ("margin-right".equals(key)) {

      final StyleValue styleValue = StyleValue.parse(value);

      if (styleValue != null) {
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            return style.setMarginRight(styleValue);
          }
        };
      }
    }

    if ("margin".equals(key)) {
      return parseMargin(value);
    }

    if ("text-indent".equals(key)) {
      final StyleValue styleValue = StyleValue.parse(value);

      if (styleValue != null) {
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            return style.setTextIndent(styleValue);
          }
        };
      }
    }

    if ("display".equals(key)) {
      try {
        final Style.DisplayStyle displayStyle = Style.DisplayStyle.valueOf(value.toUpperCase());
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            return style.setDisplayStyle(displayStyle);
          }
        };
      } catch (IllegalArgumentException ia) {
        Log.e(TAG, "Can't parse display-value: " + value);
        return null;
      }
    }

    if ("border-style".equals(key)) {
      try {
        final Style.BorderStyle borderStyle = Style.BorderStyle.valueOf(value.toUpperCase());
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            return style.setBorderStyle(borderStyle);
          }
        };
      } catch (IllegalArgumentException ia) {
        Log.e(TAG, "Could not parse border-style " + value);
        return null;
      }
    }

    if ("border-color".equals(key)) {
      try {
        final Integer borderColor = parseCSSColor(value);
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            return style.setBorderColor(borderColor);
          }
        };
      } catch (IllegalArgumentException ia) {
        Log.e(TAG, "Could not parse border-color " + value);
        return null;
      }
    }

    if ("border-width".equals(key)) {

      final StyleValue borderWidth = StyleValue.parse(value);
      if (borderWidth != null) {
        return new StyleUpdater() {
          @Override
          public Style updateStyle(Style style, HtmlSpanner spanner) {
            return style.setBorderWidth(borderWidth);
          }
        };
      } else {
        Log.e(TAG, "Could not parse border-color " + value);
        return null;
      }
    }

    if ("border".equals(key)) {
      return parseBorder(value);
    }

    Log.d(TAG, "Don't understand CSS property '" + key + "'. Ignoring it.");
    return null;
  }

  private static float translateFontSize(int fontSize) {

    switch (fontSize) {
      case 1:
        return 0.6f;
      case 2:
        return 0.8f;
      case 3:
        return 1.0f;
      case 4:
        return 1.2f;
      case 5:
        return 1.4f;
      case 6:
        return 1.6f;
      case 7:
        return 1.8f;
    }

    return 1.0f;
  }

  /**
   * Parses a border definition.
   *
   * Border definitions are a complete mess, since the order is not set.
   *
   * @param borderDefinition
   * @return
   */
  private static StyleUpdater parseBorder(String borderDefinition) {
    String[] parts = borderDefinition.split("\\s");

    StyleValue borderWidth = null;
    Integer borderColor = null;
    Style.BorderStyle borderStyle = null;

    for (String part : parts) {
      Log.d(TAG, "Trying to parse " + part);

      if (borderWidth == null) {

        borderWidth = StyleValue.parse(part);

        if (borderWidth != null) {
          Log.d(TAG, "Parsed " + part + " as border-width");
          continue;
        }
      }

      if (borderColor == null) {
        try {
          borderColor = parseCSSColor(part);
          Log.d(TAG, "Parsed " + part + " as border-color");
          continue;
        } catch (IllegalArgumentException ia) {
          //try next one
        }
      }

      if (borderStyle == null) {
        try {
          borderStyle = Style.BorderStyle.valueOf(part.toUpperCase());
          Log.d(TAG, "Parsed " + part + " as border-style");
          continue;
        } catch (IllegalArgumentException ia) {
          //next loop iteration
        }
      }

      Log.d(TAG, "Could not make sense of border-spec " + part);
    }

    final StyleValue finalBorderWidth = borderWidth;
    final Integer finalBorderColor = borderColor;
    final Style.BorderStyle finalBorderStyle = borderStyle;

    return new StyleUpdater() {
      @Override
      public Style updateStyle(Style style, HtmlSpanner spanner) {
        if (finalBorderColor != null) {
          style = style.setBorderColor(finalBorderColor);
        }

        if (finalBorderWidth != null) {
          style = style.setBorderWidth(finalBorderWidth);
        }

        if (finalBorderStyle != null) {
          style = style.setBorderStyle(finalBorderStyle);
        }

        return style;
      }
    };

  }

  private static StyleUpdater parseMargin(String marginValue) {

    String[] parts = marginValue.split("\\s");

    String bottomMarginString = "";
    String topMarginString = "";
    String leftMarginString = "";
    String rightMarginString = "";

    //See http://www.w3schools.com/css/css_margin.asp

    if (parts.length == 1) {
      bottomMarginString = parts[0];
      topMarginString = parts[0];
      leftMarginString = parts[0];
      rightMarginString = parts[0];
    } else if (parts.length == 2) {
      topMarginString = parts[0];
      bottomMarginString = parts[0];
      leftMarginString = parts[1];
      rightMarginString = parts[1];
    } else if (parts.length == 3) {
      topMarginString = parts[0];
      leftMarginString = parts[1];
      rightMarginString = parts[1];
      bottomMarginString = parts[2];
    } else if (parts.length == 4) {
      topMarginString = parts[0];
      rightMarginString = parts[1];
      bottomMarginString = parts[2];
      leftMarginString = parts[3];
    }

    final StyleValue marginBottom = StyleValue.parse(bottomMarginString);
    final StyleValue marginTop = StyleValue.parse(topMarginString);
    final StyleValue marginLeft = StyleValue.parse(leftMarginString);
    final StyleValue marginRight = StyleValue.parse(rightMarginString);

    return new StyleUpdater() {
      @Override
      public Style updateStyle(Style style, HtmlSpanner spanner) {
        Style resultStyle = style;

        if (marginBottom != null) {
          resultStyle = resultStyle.setMarginBottom(marginBottom);
        }

        if (marginTop != null) {
          resultStyle = resultStyle.setMarginTop(marginTop);
        }

        if (marginLeft != null) {
          resultStyle = resultStyle.setMarginLeft(marginLeft);
        }

        if (marginRight != null) {
          resultStyle = resultStyle.setMarginRight(marginRight);
        }

        return resultStyle;
      }
    };
  }

  public interface StyleUpdater {

    Style updateStyle(Style style, HtmlSpanner spanner);
  }

  public interface TagNodeMatcher {

    boolean matches(TagNode tagNode);
  }

  private static class TagNameMatcher implements TagNodeMatcher {

    private String tagName;

    private TagNameMatcher(String selectorString) {
      this.tagName = selectorString.trim();
    }

    @Override
    public boolean matches(TagNode tagNode) {
      return tagNode != null && tagName.equalsIgnoreCase(tagNode.getName());
    }
  }

  private static class IdMatcher implements TagNodeMatcher {

    private String id;

    private IdMatcher(String selectorString) {
      id = selectorString.substring(1);
    }

    @Override
    public boolean matches(TagNode tagNode) {

      if (tagNode == null) {
        return false;
      }

      String idAttribute = tagNode.getAttributeByName("id");
      return idAttribute != null && idAttribute.equals(id);
    }
  }

}
