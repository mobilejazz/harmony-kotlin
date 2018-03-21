/*
 * Copyright (C) 2013 Alex Kuiper
 *
 * This file is part of PageTurner
 *
 * PageTurner is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PageTurner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PageTurner.  If not, see <http://www.gnu.org/licenses/>.*
 */

package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import com.worldreader.core.R;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import jedi.functional.Command;
import jedi.option.Option;

public class TextSelectionActions implements ActionMode.Callback {

  private final TextSelectionCallback callBack;
  private final ActionModeListener actionModeListener;
  private final SelectedTextProvider selectedTextProvider;
  private final Configuration c;

  TextSelectionActions(ActionModeListener actionModeListener, TextSelectionCallback callBack, SelectedTextProvider selectedTextProvider, Configuration c) {
    this.callBack = callBack;
    this.actionModeListener = actionModeListener;
    this.selectedTextProvider = selectedTextProvider;
    this.c = c;
  }

  @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    mode.finish();
    return true;
  }

  @Override public boolean onCreateActionMode(final ActionMode mode, Menu menu) {
    if (actionModeListener != null) {
      actionModeListener.onCreateActionMode();
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      menu.removeItem(android.R.id.shareText);
    }

    if (c.isShareEnabled()) {
      menu.add(R.string.ls_generic_share).setOnMenuItemClickListener(react(mode, new Action() {
        @Override public void perform() {
          callBack.share(selectedTextProvider.getSelectedText().getOrElse(""));
        }
      })).setIcon(R.drawable.ic_share_dark);
    }

    menu.add(R.string.ls_definition).setOnMenuItemClickListener(react(mode, new Action() {
      @Override public void perform() {
        selectedTextProvider.getSelectedText().forEach(new Command<String>() {
          @Override public void execute(String text) {
            callBack.lookupDictionary(text);
          }
        });
      }
    })).setIcon(R.drawable.ic_dictionary);

    return true;
  }

  @Override public void onDestroyActionMode(ActionMode mode) {
    if (actionModeListener != null) {
      actionModeListener.onDestroyActionMode();
    }
  }

  @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      mode.setType(ActionMode.TYPE_PRIMARY);
    }

    menu.removeItem(android.R.id.selectAll);
    menu.removeItem(android.R.id.copy);

    return true;
  }

  private static OnMenuItemClickListener react(final ActionMode mode, final Action action) {
    return new OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        action.perform();
        mode.finish();
        return true;
      }
    };
  }

  public interface SelectedTextProvider {

    Option<String> getSelectedText();

    int getSelectionStart();

    int getSelectionEnd();
  }

  public interface Action {

    void perform();
  }
}
