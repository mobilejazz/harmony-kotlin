package com.mobilejazz.sample.screens.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mobilejazz.harmony.kotlin.core.domain.interactor.GetInteractor
import com.mobilejazz.harmony.kotlin.core.repository.operation.CacheSyncOperation
import com.mobilejazz.harmony.kotlin.core.repository.operation.MainSyncOperation
import com.mobilejazz.harmony.kotlin.core.repository.query.KeyQuery
import com.mobilejazz.sample.R
import com.mobilejazz.sample.core.domain.interactor.GetItemsByIdInteractor
import com.mobilejazz.sample.core.domain.model.ItemIds
import com.mobilejazz.sample.screens.ItemsAdapter
import com.mobilejazz.sample.screens.detail.ItemDetailActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HomeActivity : AppCompatActivity(), CoroutineScope {

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main

  private val adapter by lazy {
    ItemsAdapter(listener = {
      startActivity(ItemDetailActivity.getIntent(this, it))
    }, displayAllContent = false)
  }

  @Inject
  lateinit var getItemsByIdInteractor: GetItemsByIdInteractor
  @Inject
  lateinit var getAskStoriesInteractor: GetInteractor<ItemIds>


  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)

    title = "Hacker News"

    val linearLayoutManager = LinearLayoutManager(this)
    activity_home_items_rv.layoutManager = linearLayoutManager
    activity_home_items_rv.addItemDecoration(DividerItemDecoration(this, linearLayoutManager.orientation))
    activity_home_items_rv.adapter = adapter

    activity_home_swipe_refresh_srl.setOnRefreshListener {
      reloadData(true)
    }

    reloadData(false)
  }

  private suspend fun getRandomValue() = withContext(Dispatchers.Default) {
    println("entering in getRandomValue() function --> start delay")
    delay(1000)
    println("entering in getRandomValue() function --> finish delay")
    return@withContext 10
  }


  private fun reloadData(pullToRefresh: Boolean) {

    launch {
      // CouroutineScope
      // MainThread
      activity_home_swipe_refresh_srl.isRefreshing = true
      try {
        val stories = getAskStoriesInteractor(KeyQuery("ask-stories"), if (pullToRefresh) MainSyncOperation else CacheSyncOperation)
        val items = getItemsByIdInteractor(stories.ids)
        adapter.reloadData(items)
      } catch (e: Exception) {
        Log.e("Error", e.localizedMessage)
        Snackbar.make(activity_home_items_rv, "Error : " + e.localizedMessage, Snackbar.LENGTH_LONG).show()
      }

      activity_home_swipe_refresh_srl.isRefreshing = false
    }
  }
}
