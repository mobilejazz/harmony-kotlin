package com.worldreader.reader.pageturner.net.nightwhistler.ui;

import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import jedi.option.Option;

import static jedi.option.Options.option;

public class UiUtils {

  public interface Operation<A> {

    void thenDo(A arg);
  }

  public interface Action {

    void perform();
  }

  public static Operation<Action> onMenuPress(Menu menu, int elementName) {
    return onMenuPress(menu.findItem(elementName));
  }

  public static Operation<Action> onMenuPress(final MenuItem menuItem) {
    return new Operation<Action>() {
      @Override public void thenDo(final Action action) {
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
          @Override public boolean onMenuItemClick(MenuItem item) {
            action.perform();
            return true;
          }
        });
      }
    };
  }

  public static SearchView.OnQueryTextListener onQuery(final Operation<String> op) {
    return new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextSubmit(String query) {
        op.thenDo(query);
        return true;
      }

      @Override public boolean onQueryTextChange(String query) {
        return false;
      }
    };
  }

  public static MenuItem.OnActionExpandListener onCollapse(final Action onCollapse) {
    return new MenuItem.OnActionExpandListener() {
      @Override public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return true;
      }

      @Override public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        onCollapse.perform();
        return true;
      }
    };
  }

  public static Option<TextView> getTextView(View parent, int id) {
    return getView(parent, id, TextView.class);
  }

  public static Option<ImageView> getImageView(View parent, int id) {
    return getView(parent, id, ImageView.class);
  }

  public static <T extends View> Option<T> getView(View parent, int id, Class<T> viewType) {
    return option((T) parent.findViewById(id));
  }
}
