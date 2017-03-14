package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import jedi.functional.Command;
import jedi.option.Option;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.epub.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;

import static jedi.option.Options.none;

public class PreLoadTask extends QueueableAsyncTask<Void, Void, Void> {

  private PageTurnerSpine spine;
  private ResourcesLoader resourcesLoader;

  public PreLoadTask(PageTurnerSpine spine, ResourcesLoader resourcesLoader) {
    this.spine = spine;
    this.resourcesLoader = resourcesLoader;
  }

  @Override public Option<Void> doInBackground(Void... voids) {
    doInBackground();

    return none();
  }

  private void doInBackground() {
    if (spine == null) {
      return;
    }

    Option<Resource> resource = spine.getNextResource();

    resource.forEach(new Command<Resource>() {
      @Override public void execute(Resource res) {
        resourcesLoader.loadResource(res);
      }
    });
  }
}


