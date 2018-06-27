package com.worldreader.core.application.helper.image.picasso;

import android.content.Context;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.worldreader.core.application.helper.image.ImageDownloader;
import com.worldreader.core.application.helper.image.ImageLoader;
import com.worldreader.core.application.helper.image.picasso.transformations.CircleTransform;
import com.worldreader.core.application.helper.image.picasso.transformations.RoundedCornersTransformation;

import javax.inject.Inject;
import java.io.*;

public class PicassoImageLoader implements ImageLoader {

  public static final String TAG = ImageLoader.class.getSimpleName();

  private final Context context;
  private final Picasso picasso;
  private final ImageDownloader imageDownloader;

  @Inject public PicassoImageLoader(Context context, Picasso picasso, ImageDownloader imageDownloader) {
    this.context = context;
    this.picasso = picasso;
    this.imageDownloader = imageDownloader;
  }

  @Override public void load(String id, String url, ImageView imageView) {
    load(id, url, -1, imageView);
  }

  @Override public void load(String id, String url, int resPlaceholderIcon, ImageView imageView) {
    File imageCached = imageDownloader.getImage(id);
    if (imageCached != null) {

      if (imageCached.length() > 0) {
        if (resPlaceholderIcon > 0) {
          picasso.load(imageCached).placeholder(resPlaceholderIcon).into(imageView);
        } else {
          picasso.load(imageCached).into(imageView);
        }
      } else {
        if (resPlaceholderIcon > 0) {
          if (url != null && imageView != null) {
            picasso.load(url).placeholder(resPlaceholderIcon).into(imageView);
          }
        } else {
          if (url != null && imageView != null) {
            picasso.load(url).into(imageView);
          }
        }
      }
    }
  }

  @Override public void loadImageCircle(String url, int resPlaceholderIcon, ImageView imageView) {
    picasso.load(url)
        .placeholder(resPlaceholderIcon)
        .transform(new CircleTransform())
        .into(imageView);
  }

  @Override public void load(int resIcon, ImageView imageView) {
    picasso.load(resIcon).into(imageView);
  }

  @Override public void loadCover(String url, String tag, int radius, int margin, ImageView view) {
    picasso
        .load(url)
        .fit()
        .tag(tag)
        .transform(new RoundedCornersTransformation(radius, margin))
        .into(view);
  }

  @Override public void cancel(String tag) {
    picasso.cancelTag(tag);
  }

  @Override public Context getContext() {
    return context;
  }
}
