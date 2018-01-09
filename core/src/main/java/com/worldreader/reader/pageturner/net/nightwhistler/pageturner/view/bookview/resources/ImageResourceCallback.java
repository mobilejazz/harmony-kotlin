package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import com.google.common.base.Throwables;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.datasource.StreamingBookDataSource;
import com.worldreader.core.datasource.model.ContentOpfEntity;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resources;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable.AbstractFastBitmapDrawable;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable.FastBimapFactory;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bitmapdrawable.InlineFastBitmapDrawable;
import org.javatuples.Pair;

import java.io.*;
import java.util.*;

public class ImageResourceCallback {

  private static final String TAG = ImageResourceCallback.class.getSimpleName();

  private final Context context;
  private final BookMetadata bm;
  private final int height;
  private final int width;
  private final int verticalMargin;
  private final int horizontalMargin;
  private final SpannableStringBuilder builder;
  private final int start;
  private final int end;
  private final String data;
  private final StreamingBookDataSource repository;
  private final Resources resources;
  private final Listener listener;
  private final Logger logger;

  public ImageResourceCallback(Builder builder) {
    this.context = builder.context.getApplicationContext();
    this.bm = builder.bookMetadata;
    this.height = builder.height;
    this.width = builder.width;
    this.verticalMargin = builder.verticalMargin;
    this.horizontalMargin = builder.horizontalMargin;
    this.builder = builder.spannableBuilder;
    this.start = builder.start;
    this.end = builder.end;
    this.data = builder.data;
    this.listener = builder.listener;
    this.repository = builder.repository;
    this.resources = builder.resources;
    this.logger = builder.logger;
  }

  public void onPrepareFastBitmapDrawable() {
    if (!TextUtils.isEmpty(data) && data.startsWith("data:image")) { // Check if image is inlined as Base64 in the epub (this is a special case)
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
    final Resource resource = resources.getByFileName(data);

    final Map<String, ContentOpfEntity.Item> imagesResources = bm.imagesResources;
    ContentOpfEntity.Item item = null;

    if (imagesResources != null && resource != null) {
      for (ContentOpfEntity.Item elem : imagesResources.values()) {
        if (elem.href.contains(resource.getHref())) {
          item = elem;
          break;
        }
      }
    }

    // Try to load image sizes from the content.opf
    Integer width = item != null && !TextUtils.isEmpty(item.width) ? Integer.valueOf(item.width) : null;
    Integer height = item != null && !TextUtils.isEmpty(item.height) ? Integer.valueOf(item.height) : null;

    // If for some reason we can't load properly the size...
    if (width == null || height == null) {
      if (bm.mode == BookMetadata.FILE_MODE) {
        // Try to load from file itself as we have the InputStream
        try {
          // Loading this directly uses an optimized version of InputStream (or at least it should be)
          final InputStream inputStream = resource.getInputStream();

          final BitmapFactory.Options Bitmp_Options = new BitmapFactory.Options();
          Bitmp_Options.inJustDecodeBounds = true;

          inputStream.mark(inputStream.available());

          BitmapFactory.decodeResourceStream(context.getResources(), new TypedValue(), inputStream, new Rect(), Bitmp_Options);

          width = Bitmp_Options.outWidth;
          height = Bitmp_Options.outHeight;
        } catch (Exception e) {
          logger.e(TAG, "Can't decode properly size on InputSteam: " + Throwables.getStackTraceAsString(e));
          width = 480;
          height = 800;
        }
      } else {
        // Load default harcoded sizes as we don't access to the InputStream (until it goes by network)
        width = 480;
        height = 800;
      }
    }

    final Pair<Integer, Integer> sizes = calculateProperImageSize(width, height);
    final int finalWidth = sizes.getValue0();
    final int finalHeight = sizes.getValue1();

    final AbstractFastBitmapDrawable drawable = FastBimapFactory.create(bm, resource, finalWidth, finalHeight, repository, logger);

    notifyListener(drawable, builder, start, end);
  }

  private void notifyListener(Drawable drawable, SpannableStringBuilder builder, int start, int end) {
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

    private Context context;
    private BookMetadata bookMetadata;
    private int height;
    private int width;
    private int verticalMargin;
    private int horizontalMargin;
    private SpannableStringBuilder spannableBuilder;
    private int start;
    private int end;
    private String data;
    private StreamingBookDataSource repository;
    private Resources resources;
    private Listener listener;
    private Logger logger;

    public Builder withContext(Context context) {
      this.context = context;
      return this;
    }

    public Builder withMetadata(BookMetadata metadata) {
      this.bookMetadata = metadata;
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

    public Builder withRepository(StreamingBookDataSource repository) {
      this.repository = repository;
      return this;
    }

    public Builder withResources(Resources resources) {
      this.resources = resources;
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
