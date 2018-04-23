package net.nightwhistler.pageturner.view.bookview;

import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import com.worldreader.core.R;
import com.worldreader.reader.wr.configuration.ReaderConfig;
import jedi.functional.Command;
import jedi.option.Option;

public class TextSelectionActions implements ActionMode.Callback {

  private final TextSelectionCallback callBack;
  private final ActionModeListener actionModeListener;
  private final SelectedTextProvider selectedTextProvider;
  private final ReaderConfig c;

  TextSelectionActions(ActionModeListener actionModeListener, TextSelectionCallback callBack, SelectedTextProvider selectedTextProvider, ReaderConfig c) {
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
