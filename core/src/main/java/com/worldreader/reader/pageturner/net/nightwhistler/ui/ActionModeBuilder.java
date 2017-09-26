package com.worldreader.reader.pageturner.net.nightwhistler.ui;

import android.support.v4.app.FragmentActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Builder class for ActionMode callbacks.
 *
 * Allows a more functional way of building action modes.
 *
 * @author Alex Kuiper
 */
public class ActionModeBuilder {

  public interface ActionModeAction<A> {

    boolean perform(ActionMode actionMode, A item);
  }

  public interface DestroyActionModeCommand {

    void perform(ActionMode mode);
  }

  private ActionModeAction<Menu> createAction;
  private ActionModeAction<Menu> prepareAction;
  private ActionModeAction<MenuItem> clickedAction;
  private DestroyActionModeCommand destroyAction;

  /**
   * Creates a blank builder
   */
  public ActionModeBuilder() {

  }

  /**
   * Creates an ActionModeBuilder which uses the given string
   * as a title for the ActionMode
   */
  public ActionModeBuilder(String title) {
    setTitle(title);
  }

  /**
   * Creates an ActionModeBuilder which uses the given
   * resource id for the title.
   */
  public ActionModeBuilder(int titleResourceId) {
    setTitle(titleResourceId);
  }

  public ActionModeBuilder setTitle(final String title) {
    this.prepareAction = new ActionModeAction<Menu>() {
      @Override public boolean perform(ActionMode actionMode, Menu menu) {
        actionMode.setTitle(title);
        return true;
      }
    };

    return this;
  }

  public ActionModeBuilder setTitle(final int titleResourceId) {
    this.prepareAction = new ActionModeAction<Menu>() {
      @Override public boolean perform(ActionMode actionMode, Menu menu) {
        actionMode.setTitle(titleResourceId);
        return true;
      }
    };

    return this;
  }

  public ActionModeBuilder setOnActionItemClickedAction(ActionModeAction<MenuItem> clickedAction) {
    this.clickedAction = clickedAction;
    return this;
  }

  public ActionModeBuilder setOnCreateAction(ActionModeAction<Menu> createAction) {
    this.createAction = createAction;
    return this;
  }

  public ActionModeBuilder setOnDestroyAction(DestroyActionModeCommand destroyAction) {
    this.destroyAction = destroyAction;
    return this;
  }

  public ActionModeBuilder setOnPrepareAction(ActionModeAction<Menu> prepareAction) {
    this.prepareAction = prepareAction;
    return this;
  }

  public void build(FragmentActivity activity) {
    activity.startActionMode(new ActionMode.Callback() {
      @Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return createAction != null && createAction.perform(mode, menu);
      }

      @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return prepareAction != null && prepareAction.perform(mode, menu);
      }

      @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return clickedAction != null && clickedAction.perform(mode, item);
      }

      @Override public void onDestroyActionMode(ActionMode mode) {
        if (destroyAction != null) {
          destroyAction.perform(mode);
        }
      }
    });
  }
}
