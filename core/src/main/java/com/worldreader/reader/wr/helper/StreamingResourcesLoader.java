package com.worldreader.reader.wr.helper;

import com.mobilejazz.logger.library.Logger;
import com.worldreader.core.domain.model.BookMetadata;
import com.worldreader.core.domain.model.StreamingResource;
import com.worldreader.core.domain.repository.StreamingBookRepository;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.ResourcesLoader;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class StreamingResourcesLoader implements ResourcesLoader {

  public static final String TAG = StreamingResourcesLoader.class.getSimpleName();

  private final BookMetadata bookMetadata;
  private StreamingBookRepository dataSource;
  private Logger logger;
  private List<Holder> callbacks = new ArrayList<>();
  private PageTurnerSpine spine;

  public StreamingResourcesLoader(BookMetadata bookMetadata, StreamingBookRepository dataSource, Logger logger) {
    this.bookMetadata = bookMetadata;
    this.dataSource = dataSource;
    this.logger = logger;
  }

  @Override public InputStream loadResource(String path) {
    StreamingResource bookResource;
    try {
      bookResource = dataSource.getBookResource(bookMetadata.getBookId(), bookMetadata, URLDecoder.decode(path));
    } catch (Throwable throwable) {
      bookResource = StreamingResource.create(null);
    }

    if (bookResource.getInputStream() == null) {
      logger.d(TAG, "Book resource is null with path: " + path);
    }

    return bookResource.getInputStream();
  }

  @Override public InputStream loadResource(Resource resource) {
    return this.loadResource(resource.getHref());
  }

  @Override public void loadImageResources() {
    for (Holder holder : callbacks) {
      holder.callback.onLoadImageResource(holder.href, null, dataSource, bookMetadata);
    }
    callbacks.clear();
  }

  @Override public void registerImageCallback(String resolvedHref, ImageResourceCallback imageCallback) {
    this.callbacks.add(new Holder(resolvedHref, imageCallback));
  }

  @Override public void registerSpine(final PageTurnerSpine spine) {
    this.spine = spine;
  }

  private static class Holder {

    Holder(String href, ImageResourceCallback callback) {
      this.href = href;
      this.callback = callback;
    }

    String href;
    ImageResourceCallback callback;
  }

}
