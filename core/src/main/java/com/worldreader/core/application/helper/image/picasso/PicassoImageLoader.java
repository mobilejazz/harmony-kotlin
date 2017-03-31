package com.worldreader.core.application.helper.image.picasso;

import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.worldreader.core.application.helper.image.ImageDownloader;
import com.worldreader.core.application.helper.image.ImageLoader;
import com.worldreader.core.application.helper.image.picasso.transformations.CircleTransform;

import javax.inject.Inject;
import java.io.*;

public class PicassoImageLoader implements ImageLoader {

  public static final String TAG = ImageLoader.class.getSimpleName();

  private final Picasso picasso;
  private final ImageDownloader imageDownloader;

  @Inject public PicassoImageLoader(Picasso picasso, ImageDownloader imageDownloader) {
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
          if (url != null) {
            picasso.load(url).placeholder(resPlaceholderIcon).into(imageView);
          }
        } else {
          if (url != null) {
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
}
