package com.mobilejazz.sample.screens.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.harmony.kotlin.android.application.helpers.LocalizedStrings
import com.mobilejazz.sample.R
import com.mobilejazz.sample.core.domain.interactor.GetItemsByIdInteractor
import com.mobilejazz.sample.core.domain.model.Item
import com.mobilejazz.sample.screens.ItemsAdapter
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ItemDetailActivity : AppCompatActivity(), CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main

  companion object {
    const val ITEM_KEY = "item-key"

    fun getIntent(context: Context, item: Item): Intent = Intent(context, ItemDetailActivity::class.java).apply { putExtra(ITEM_KEY, item) }
  }

  @Inject
  lateinit var getItemsByIdInteractor: GetItemsByIdInteractor

  @Inject
  lateinit var localizedStrings: LocalizedStrings

  private val adapter by lazy {
    ItemsAdapter(listener = {
      // Nothing to do
    }, displayAllContent = true)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_item_detail)

    title = "Hacker News"

    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val item = intent.extras.get(ITEM_KEY) as Item

    activity_detail_item_title_tv.text = item.title

    activity_detail_items_rv.layoutManager = LinearLayoutManager(this)
    activity_detail_items_rv.adapter = adapter

    activity_detail_item_by_tv.text = "by: ${item.by}"

    item.text?.let {
      activity_detail_item_description_tv.text = Html.fromHtml(it)
    }

    item.kids?.let {
      activity_detail_items_comments_tv.text = localizedStrings.get(R.string.loading)

      loadComments(it)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        finish()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun loadComments(ids: List<Int>) {
    launch {
      val items = getItemsByIdInteractor(ids)
      adapter.reloadData(items)

      activity_detail_items_comments_tv.text = "${ids.size} " + localizedStrings.getPlural(R.plurals.comments, ids.size)
    }
  }
}
