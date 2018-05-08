package com.mobilejazz.kotlin.core.sample.app.ui.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mobilejazz.kotlin.core.sample.domain.model.Item


class ItemsAdapter(val items: List<Item> = ArrayList()) : BaseAdapter() {

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val view = convertView ?: LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
    val text = view.findViewById<TextView>(android.R.id.text1)
    text.text = getItem(position).name
    return view
  }

  override fun getItem(position: Int): Item {
    return items[position]
  }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  override fun getCount(): Int {
    return items.size
  }
}