package com.mobilejazz.kotlin.core.sample.app.ui.items

import android.os.Bundle
import com.mobilejazz.kotlin.core.sample.R
import com.mobilejazz.kotlin.core.ui.base.view.BaseMVPActivity


class ItemsActivity : BaseMVPActivity<ItemsPresenter, ItemsPresenter.View>() {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_items)


  }

  override fun onDisplayError(throwable: Throwable) {
    // TODO
  }
}