package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks;

import android.text.Spanned;
import com.mobilejazz.logger.library.Logger;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.changestrategy.PageChangeStrategy;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoader;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.TextLoader;
import jedi.option.Option;

import static jedi.option.Options.none;

public class LoadStreamingTextTask extends QueueableAsyncTask<Resource, Void, Spanned> {

  private static final String TAG = LoadStreamingTextTask.class.getSimpleName();

  private final PageChangeStrategy strategy;
  private final TextLoader textLoader;
  private final ResourcesLoader resourcesLoader;
  private final Logger logger;

  public LoadStreamingTextTask(final PageChangeStrategy strategy, final TextLoader textLoader, final ResourcesLoader resourcesLoader, final Logger logger) {
    this.strategy = strategy;
    this.textLoader = textLoader;
    this.resourcesLoader = resourcesLoader;
    this.logger = logger;
  }

  @Override public Option<Spanned> doInBackground(Resource... resources) {
    return none();
  }
}
