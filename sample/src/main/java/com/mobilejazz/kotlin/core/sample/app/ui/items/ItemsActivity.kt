package com.mobilejazz.kotlin.core.sample.app.ui.items

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mobilejazz.kotlin.core.sample.R
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.ui.base.view.BaseMVPActivity
import kotlinx.android.synthetic.main.activity_items.*

class ItemsActivity : BaseMVPActivity<ItemsPresenter, ItemsPresenter.View>(), ItemsPresenter.View {


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_items)
  }

  override fun onDisplayItems(items: List<Item>) {
    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items.map { it.toString() })
    items_lv.adapter = ItemsAdapter(items)
  }


  override fun onDisplayError(throwable: Throwable) {
    Toast.makeText(this, throwable.cause.toString(), Toast.LENGTH_SHORT).show()
  }
}