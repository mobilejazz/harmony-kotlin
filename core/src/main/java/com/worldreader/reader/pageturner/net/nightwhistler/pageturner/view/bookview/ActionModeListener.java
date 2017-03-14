package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

/** Informs about contextual action menu preparation to those who wants to listen to this events. */
public interface ActionModeListener {

  void onPrepareActionMode();

  void onCreateActionMode();

  void onDestroyActionMode();

}
