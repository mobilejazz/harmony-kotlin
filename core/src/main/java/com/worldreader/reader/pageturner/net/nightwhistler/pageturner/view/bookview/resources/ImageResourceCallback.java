package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.datasource.model.ContentOpfEntity;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable.InlineFastBitmapDrawable;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable.StreamingFastBitmapDrawable;
import org.javatuples.Pair;

import java.io.*;
import java.util.*;

public class ImageResourceCallback {

  private static final String TAG = ImageResourceCallback.class.getSimpleName();

  private final BookMetadata bookMetadata;
  private final StreamingBookRepository repository;
  private final int height;
  private final int width;
  private final int verticalMargin;
  private final int horizontalMargin;
  private final SpannableStringBuilder builder;
  private final int start;
  private final int end;
  private final String data;
  private final Listener listener;
  private final Logger logger;

  public ImageResourceCallback(Builder builder) {
    this.bookMetadata = builder.bookMetadata;
    this.repository = builder.repository;
    this.height = builder.height;
    this.width = builder.width;
    this.verticalMargin = builder.verticalMargin;
    this.horizontalMargin = builder.horizontalMargin;
    this.builder = builder.spannableBuilder;
    this.start = builder.start;
    this.end = builder.end;
    this.data = builder.data;
    this.listener = builder.listener;
    this.logger = builder.logger;
  }

  public void onPrepareFastBitmapDrawable() {
    if (!TextUtils.isEmpty(data) && data.startsWith("data:image")) {
      onPrepareBitmapDrawableFromInline();
    } else {
      onPrepareBitmapDrawableFromResource();
    }
  }

  private void onPrepareBitmapDrawableFromInline() {
    final String dataString = data.substring(data.indexOf(',') + 1);
    final ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(dataString, Base64.DEFAULT));

    final InlineFastBitmapDrawable drawable = new InlineFastBitmapDrawable(is, width, height, logger);

    notifyListener(drawable, builder, start, end);
  }

  private void onPrepareBitmapDrawableFromResource() {
    final Map<String, ContentOpfEntity.Item> imagesResources = bookMetadata.imagesResources;
    final ContentOpfEntity.Item item = imagesResources != null ? imagesResources.get(data) : null;

    final Integer width = item != null && !TextUtils.isEmpty(item.width) ? Integer.valueOf(item.width) : 480;
    final Integer height = item != null && !TextUtils.isEmpty(item.height) ? Integer.valueOf(item.height) : 800;

    final Pair<Integer, Integer> sizes = calculateProperImageSize(width, height);
    final int finalWidth = sizes.getValue0();
    final int finalHeight = sizes.getValue1();

    final StreamingFastBitmapDrawable drawable = new StreamingFastBitmapDrawable(finalWidth, finalHeight, bookMetadata, repository, data, logger);

    notifyListener(drawable, builder, start, end);
  }

  protected void notifyListener(Drawable drawable, SpannableStringBuilder builder, int start, int end) {
    if (listener != null) {
      listener.onBitmapDrawableCreated(drawable, builder, start, end);
    }
  }

  private Pair<Integer, Integer> calculateProperImageSize(int originalWidth, int originalHeight) {
    final int screenHeight = height - (verticalMargin * 2);
    final int screenWidth = width - (horizontalMargin * 2);

    if (originalWidth < screenWidth && originalHeight < screenHeight) {
      return Pair.with(originalWidth, originalHeight);
    }

    final float ratio = (float) originalWidth / (float) originalHeight;

    int targetHeight = screenHeight - 1;
    int targetWidth = (int) (targetHeight * ratio);

    if (targetWidth > screenWidth - 1) {
      targetWidth = screenWidth - 1;
      targetHeight = (int) (targetWidth * (1 / ratio));
    }

    Log.d(TAG, "Rescaling from " + originalWidth + "x" + originalHeight + " to " + targetWidth + "x" + targetHeight);

    return Pair.with(targetWidth, targetHeight);
  }

  public static class Builder {

    private BookMetadata bookMetadata;
    private StreamingBookRepository repository;
    private int height;
    private int width;
    private int verticalMargin;
    private int horizontalMargin;
    private SpannableStringBuilder spannableBuilder;
    private int start;
    private int end;
    private String data;
    private Listener listener;
    private Logger logger;

    public Builder withMetadata(BookMetadata metadata) {
      this.bookMetadata = metadata;
      return this;
    }

    public Builder withRepository(StreamingBookRepository repository) {
      this.repository = repository;
      return this;
    }

    public Builder withHeight(int height) {
      this.height = height;
      return this;
    }

    public Builder withWidth(int width) {
      this.width = width;
      return this;
    }

    public Builder withVerticalMargin(int verticalMargin) {
      this.verticalMargin = verticalMargin;
      return this;
    }

    public Builder withHorizontal(int horizontalMargin) {
      this.horizontalMargin = horizontalMargin;
      return this;
    }

    public Builder withSpannableBuilder(SpannableStringBuilder builder) {
      this.spannableBuilder = builder;
      return this;
    }

    public Builder withStart(int start) {
      this.start = start;
      return this;
    }

    public Builder withEnd(int end) {
      this.end = end;
      return this;
    }

    public Builder withData(String data) {
      this.data = data;
      return this;
    }

    public Builder withListener(Listener listener) {
      this.listener = listener;
      return this;
    }

    public Builder withLogger(Logger logger) {
      this.logger = logger;
      return this;
    }

    public ImageResourceCallback create() {
      return new ImageResourceCallback(this);
    }

  }

  public interface Listener {

    void onBitmapDrawableCreated(Drawable drawable, SpannableStringBuilder builder, int start, int end);
  }

}
