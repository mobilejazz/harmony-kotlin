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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import com.worldreader.core.R;
import com.worldreader.reader.pageturner.net.nightwhistler.ui.UiUtils;
import jedi.functional.Command;
import jedi.option.Option;

@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class TextSelectionActions
    implements ActionMode.Callback {

  private TextSelectionCallback callBack;
  private ActionModeListener actionModeListener;
  private SelectedTextProvider selectedTextProvider;

  private Context context;

  public interface SelectedTextProvider {
    Option<String> getSelectedText();

    int getSelectionStart();

    int getSelectionEnd();
  }

  public TextSelectionActions(Context context, ActionModeListener actionModeListener,
      TextSelectionCallback callBack, SelectedTextProvider selectedTextProvider) {
    this.callBack = callBack;
    this.actionModeListener = actionModeListener;
    this.context = context;
    this.selectedTextProvider = selectedTextProvider;
  }

  @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
    mode.finish();
    return true;
  }

  private static OnMenuItemClickListener react(final ActionMode mode, final UiUtils.Action action) {
    return new OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        action.perform();
        mode.finish();
        return true;
      }
    };
  }

  @Override public boolean onCreateActionMode(final ActionMode mode, Menu menu) {
    if (actionModeListener != null) {
      actionModeListener.onCreateActionMode();
    }

    menu.removeItem(android.R.id.selectAll);
    menu.removeItem(android.R.id.copy);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      menu.removeItem(android.R.id.shareText);
    }

    menu.add(R.string.share).setOnMenuItemClickListener(react(mode, new UiUtils.Action() {
      @Override public void perform() {
        callBack.share(selectedTextProvider.getSelectionStart(),
            selectedTextProvider.getSelectionEnd(),
            selectedTextProvider.getSelectedText().getOrElse(""));
      }
    })).setIcon(R.drawable.ic_share_dark);

    menu.add(R.string.definition).setOnMenuItemClickListener(react(mode, new UiUtils.Action() {
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
    if (actionModeListener != null) {
      actionModeListener.onPrepareActionMode();
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      mode.setType(ActionMode.TYPE_PRIMARY);
    }

    return true;
  }
}
