package com.mobilejazz.sample.screens.home

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.harmony.kotlin.data.operation.CacheSyncOperation
import com.harmony.kotlin.data.operation.MainSyncOperation
import com.harmony.kotlin.data.query.KeyQuery
import com.harmony.kotlin.domain.interactor.GetInteractor
import com.mobilejazz.sample.R
import com.mobilejazz.sample.core.domain.interactor.GetItemsByIdInteractor
import com.mobilejazz.sample.core.domain.model.ItemIds
import com.mobilejazz.sample.screens.ItemsAdapter
import com.mobilejazz.sample.screens.detail.ItemDetailActivity
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

  private fun Button.clicks(): Flow<Unit> = flow { setOnClickListener { launch { emit(Unit) } } }


  private fun reloadData(pullToRefresh: Boolean) {
    launch {
      activity_home_swipe_refresh_srl.isRefreshing = true

      flowOf(getAskStoriesInteractor(KeyQuery("ask-stories"), if (pullToRefresh) MainSyncOperation else CacheSyncOperation))
        .flatMapMerge { flowOf(getItemsByIdInteractor(it.ids)) }
        .catch {
          Log.e("Error", it.localizedMessage)
          Snackbar.make(activity_home_items_rv, "Error : " + it.localizedMessage, Snackbar.LENGTH_LONG).show()
        }
        .collect {
          adapter.reloadData(it)
          activity_home_swipe_refresh_srl.isRefreshing = false
        }
      // CouroutineScope
      // MainThread
      /*activity_home_swipe_refresh_srl.isRefreshing = true
      try {
        val stories = getAskStoriesInteractor(KeyQuery("ask-stories"), if (pullToRefresh) MainSyncOperation else CacheSyncOperation)
        val items = getItemsByIdInteractor(stories.ids)
        adapter.reloadData(items)
      } catch (e: Exception) {
        Log.e("Error", e.localizedMessage)
        Snackbar.make(activity_home_items_rv, "Error : " + e.localizedMessage, Snackbar.LENGTH_LONG).show()
      }

      activity_home_swipe_refresh_srl.isRefreshing = false*/
    }
  }
}
