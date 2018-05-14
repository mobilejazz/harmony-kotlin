package com.mobilejazz.kotlin.core.sample.app.ui.items

import android.os.Bundle
import android.widget.ArrayAdapter
import com.mobilejazz.kotlin.core.repository.*
import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.mapper.Mapper
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

  }
}