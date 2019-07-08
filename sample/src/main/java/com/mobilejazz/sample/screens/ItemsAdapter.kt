package com.mobilejazz.sample.screens

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobilejazz.sample.R
import com.mobilejazz.sample.core.domain.model.Item
import kotlinx.android.synthetic.main.item_cell.view.*

class ItemsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  @SuppressLint("SetTextI18n")
  fun bind(item: Item, listener: (Item) -> Unit, displayAllContent: Boolean) {

    if (item.title == null) {
      itemView.item_cell_title_tv.visibility = View.GONE
    } else {
      itemView.item_cell_title_tv.visibility = View.VISIBLE
      itemView.item_cell_title_tv.text = item.title
    }

    itemView.item_cell_by_tv.text = "by: ${item.by}"

    if (item.text == null) {
      itemView.item_cell_description_tv.visibility = View.GONE
    } else {
      if (displayAllContent) {
        itemView.item_cell_description_tv.maxLines = Int.MAX_VALUE
      }

      itemView.item_cell_description_tv.visibility = View.VISIBLE
      itemView.item_cell_description_tv.text = Html.fromHtml(item.text)
    }

    itemView.setOnClickListener {
      listener(item)
    }
  }

}

class ItemsAdapter(private val listener: (Item) -> Unit, val displayAllContent: Boolean) : RecyclerView.Adapter<ItemsViewHolder>() {

  private var items: List<Item> = mutableListOf()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder = ItemsViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_cell, parent, false))

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
    holder.bind(items[position], listener, displayAllContent)
  }

  fun reloadData(data: List<Item>) {
    items = data
    notifyDataSetChanged()
  }

}