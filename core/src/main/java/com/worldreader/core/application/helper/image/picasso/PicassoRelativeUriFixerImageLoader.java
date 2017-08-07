package com.worldreader.core.application.helper.image.picasso;

import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.worldreader.core.application.helper.image.ImageDownloader;
import javax.inject.Inject;

public class PicassoRelativeUriFixerImageLoader extends PicassoImageLoader {

  private final String endpoint;

  @Inject public PicassoRelativeUriFixerImageLoader(Picasso picasso, String endpoint, ImageDownloader imageDownloader) {
    super(picasso, imageDownloader);
    this.endpoint = endpoint;
  }

  @Override public void load(String id, String url, ImageView imageView) {
    super.load(id, fixUrl(url), imageView);
  }

  @Override public void load(String id, String url, int resPlaceholderIcon, ImageView imageView) {
    super.load(id, fixUrl(url), resPlaceholderIcon, imageView);
  }

  @Override public void load(int resIcon, ImageView imageView) {
    super.load(resIcon, imageView);
  }

  private String fixUrl(String url) {
    if (url != null && url.startsWith("/")) {
      url = endpoint.concat(url);
    }
    return url;
  }
}
