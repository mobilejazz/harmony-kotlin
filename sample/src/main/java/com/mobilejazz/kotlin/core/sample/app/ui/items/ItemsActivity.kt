package com.mobilejazz.kotlin.core.sample.app.ui.items

import android.os.Bundle
import android.widget.Toast
import com.mobilejazz.kotlin.core.sample.R
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.ui.base.view.BaseMVPActivity
import kotlinx.android.synthetic.main.activity_items.*

class ItemsActivity : BaseMVPActivity<ItemsPresenter, ItemsPresenter.View>(), ItemsPresenter.View {

  override fun getContentViewResId(): Int {
    return R.layout.activity_items
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    swipe_refresh_ly.setOnRefreshListener {
      presenter.loadItems()
    }
  }

  override fun onDisplayLoading() {
    if (items_lv.adapter?.isEmpty == true) {
      load_content_ly.showLoading()
    }
  }

  override fun onDisplayItems(items: List<Item>) {
    items_lv.adapter = ItemsAdapter(items)
    load_content_ly.showContent(!swipe_refresh_ly.isRefreshing)

    swipe_refresh_ly.isRefreshing = false

  }

  override fun onDisplayError(throwable: Throwable) {
    val errorMessage = throwable.cause.toString();

    if (!swipe_refresh_ly.isRefreshing) {
      load_content_ly.showError(errorMessage, R.string.retry) {
        presenter.loadItems()
      }
    } else {
      Toast.makeText(this@ItemsActivity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    swipe_refresh_ly.isRefreshing = false

  }
}