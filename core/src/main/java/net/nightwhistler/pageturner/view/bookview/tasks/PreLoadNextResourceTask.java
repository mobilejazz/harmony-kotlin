package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.tasks;

import nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.wr.models.PageTurnerSpine;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.scheduling.QueueableAsyncTask;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview.resources.ResourcesLoader;
import jedi.functional.Command;
import jedi.option.Option;

import static jedi.option.Options.none;

public class PreLoadNextResourceTask extends QueueableAsyncTask<Void, Void, Void> {

  private PageTurnerSpine spine;
  private ResourcesLoader resourcesLoader;

  public PreLoadNextResourceTask(PageTurnerSpine spine, ResourcesLoader resourcesLoader) {
    this.spine = spine;
    this.resourcesLoader = resourcesLoader;
  }

  @Override public Option<Void> doInBackground(Void... voids) {
    if (spine == null) {
      return none();
    }

    final Option<Resource> resource = spine.getNextResource();
    resource.forEach(new Command<Resource>() {
      @Override public void execute(Resource res) {
        resourcesLoader.loadResource(res);
      }
    });

    return none();
  }

}


