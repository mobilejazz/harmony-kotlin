package com.mobilejazz.kmmsample.application.ui.screen.hackerpost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobilejazz.kmmsample.application.R
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import java.time.format.DateTimeFormatter
import java.util.Locale

class HackerPostsAdapter(
  private val dataSet: List<HackerNewsPost>,
  private val onItemClicked: (HackerNewsPost) -> Unit
) :
  RecyclerView.Adapter<HackerPostsAdapter.ViewHolder>() {
  private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val titleTv: TextView = view.findViewById(R.id.hacker_post_item_title)
    private val dateTv: TextView = view.findViewById(R.id.hacker_post_item_date)

    fun bind(post: HackerNewsPost) {
      titleTv.text = post.title
      dateTv.text = formatter.format(post.time.toJavaLocalDateTime())
    }
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(viewGroup.context)
      .inflate(R.layout.hacker_post_item, viewGroup, false)

    return ViewHolder(view)
  }

  override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
    val hackerNewsPost = dataSet[position]
    viewHolder.bind(hackerNewsPost)
    viewHolder.itemView.setOnClickListener { onItemClicked(hackerNewsPost) }
  }

  override fun getItemCount() = dataSet.size
}
