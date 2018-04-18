package net.nightwhistler.htmlspanner.handlers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import net.nightwhistler.htmlspanner.SpanStack;
import org.htmlcleaner.TagNode;

import java.util.*;

/**
 * Handles simple HTML tables.
 *
 * Since it renders these tables itself, it needs to know things like font size and text colour to use.
 */
public class TableHandler extends TagNodeHandler {

  private int tableWidth = 400;
  private Typeface typeFace = Typeface.DEFAULT;
  private float textSize = 16f;
  private int textColor = Color.BLACK;

  private static final int PADDING = 5;

  /**
   * Sets how wide the table should be.
   *
   * @param tableWidth
   */
  public void setTableWidth(int tableWidth) {
    this.tableWidth = tableWidth;
  }

  /**
   * Sets the text colour to use.
   *
   * Default is black.
   *
   * @param textColor
   */
  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }

  /**
   * Sets the font size to use.
   *
   * Default is 16f.
   *
   * @param textSize
   */
  public void setTextSize(float textSize) {
    this.textSize = textSize;
  }

  /**
   * Sets the TypeFace to use.
   *
   * Default is Typeface.DEFAULT
   *
   * @param typeFace
   */
  public void setTypeFace(Typeface typeFace) {
    this.typeFace = typeFace;
  }

  @Override
  public boolean rendersContent() {
    return true;
  }

  @Override public void beforeChildren(TagNode node, SpannableStringBuilder builder, SpanStack spanStack) {
    super.beforeChildren(node, builder, spanStack);
  }

  @Override
  public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end, SpanStack spanStack) {
    final Table table = getTable(node);
    final TextPaint textPaint = getTextPaint();

    final List<List<Spanned>> rows = table.getRows();
    final int size = rows.size();
    for (int i = 0; i < size; i++) {
      final List<Spanned> row = rows.get(i);

      // Add space
      builder.append("\uFFFC");

      // Create row drawable
      final TableRowDrawable drawable = new TableRowDrawable(table, row, textColor, tableWidth, textPaint);

      // Add the span to the span stack
      spanStack.pushSpan(new ImageSpan(drawable), start + i, builder.length());
    }

    // We add an empty last row to work around a rendering issue where the last row would appear detached.
    builder.append("\uFFFC");

    final ArrayList<Spanned> emptyRow = new ArrayList<>();
    final Drawable drawable = new TableRowDrawable(table, emptyRow, textColor, tableWidth, textPaint);
    drawable.setBounds(0, 0, tableWidth, 1);

    builder.setSpan(new ImageSpan(drawable), builder.length() - 1, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    builder.setSpan(new AlignmentSpan() {
      @Override
      public Alignment getAlignment() {
        return Alignment.ALIGN_CENTER;
      }
    }, start, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    builder.append("\n");
  }

  private Table getTable(TagNode node) {
    final Table result = new Table(node);
    readNode(node, result);
    return result;
  }

  private void readNode(Object node, Table table) {
    // We can't handle plain content nodes within the table.
    if (node instanceof TagNode) {
      final TagNode tagNode = (TagNode) node;

      if (tagNode.getName().equals("td")) {
        final Spanned result = getSpanner().fromTagNode(tagNode, null);
        table.addCell(result);
        return;
      }

      if (tagNode.getName().equals("tr")) {
        table.addRow();
      }

      for (Object child : tagNode.getAllChildren()) {
        readNode(child, table);
      }
    }
  }

  private TextPaint getTextPaint() {
    final TextPaint textPaint = new TextPaint();
    textPaint.setColor(textColor);
    textPaint.linkColor = textColor;
    textPaint.setAntiAlias(true);
    textPaint.setTextSize(textSize);
    textPaint.setTypeface(typeFace);
    return textPaint;
  }

  /**
   * Drawable of the table, which does the actual rendering.
   */
  private static class TableRowDrawable extends Drawable {

    private final Table table;
    private final List<Spanned> row;
    private final int textColor;
    private final int rowHeight;
    private final int tableWidth;
    private final TextPaint textPaint;

    TableRowDrawable(Table table, List<Spanned> row, int textColor, int tableWidth, TextPaint textPaint) {
      this.table = table;
      this.row = row;
      this.textColor = textColor;
      this.tableWidth = tableWidth;
      this.textPaint = textPaint;
      this.rowHeight = calculateRowHeight(row);
      setBounds(0, 0, getIntrinsicWidth(), getIntrinsicHeight());
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
      final Paint paint = new Paint();
      paint.setColor(textColor);
      paint.setStyle(Style.STROKE);

      final int numberOfColumns = row.size();
      if (numberOfColumns == 0) {
        return;
      }

      int columnWidth = tableWidth / numberOfColumns;
      int offset;
      for (int i = 0; i < numberOfColumns; i++) {
        offset = i * columnWidth;

        if (table.shouldDrawBorder()) {
          // The rect is open at the bottom, so there's a single line between rows.
          canvas.drawRect(offset, 0, offset + columnWidth, rowHeight, paint);
        }

        final Spanned cell = row.get(i);
        final int width = (columnWidth - 2 * PADDING);

        final StaticLayout layout = new StaticLayout(cell, textPaint, width, Alignment.ALIGN_NORMAL, 1f, 0f, true);

        // Draw the table cell
        canvas.translate(offset + PADDING, 0);
        layout.draw(canvas);
        canvas.translate(-1 * (offset + PADDING), 0);
      }
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    public int getOpacity() {
      return PixelFormat.OPAQUE;
    }

    @Override
    public int getIntrinsicWidth() {
      return tableWidth;
    }

    @Override
    public int getIntrinsicHeight() {
      return rowHeight;
    }

    private int calculateRowHeight(List<Spanned> row) {
      if (row.size() == 0) {
        return 0;
      }

      final int columnWidth = tableWidth / row.size();
      int rowHeight = 0;

      for (Spanned cell : row) {
        final int width = columnWidth - 2 * PADDING;
        final StaticLayout l = new StaticLayout(cell, textPaint, width, Alignment.ALIGN_NORMAL, 1f, 0f, true);
        final int height = l.getHeight();

        if (height > rowHeight) {
          rowHeight = height;
        }
      }

      return rowHeight;
    }
  }

  private static class Table {

    private final TagNode node;
    private final List<List<Spanned>> content = new ArrayList<>();

    private Table(TagNode node) {
      this.node = node;
    }

    boolean shouldDrawBorder() {
      final String border = node.getAttributeByName("border");
      return !TextUtils.isEmpty(border) && !"0".equals(border);
    }

    void addRow() {
      content.add(new ArrayList<Spanned>());
    }

    List<Spanned> getBottomRow() {
      return content.get(content.size() - 1);
    }

    List<List<Spanned>> getRows() {
      return content;
    }

    void addCell(Spanned text) {
      if (content.isEmpty()) {
        throw new IllegalStateException("No rows added yet");
      }

      getBottomRow().add(text);
    }
  }

}
