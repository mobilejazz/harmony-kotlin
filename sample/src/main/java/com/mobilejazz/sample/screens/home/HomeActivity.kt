package com.mobilejazz.sample.screens.home

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.mobilejazz.harmony.kotlin.android.threading.extension.onCompleteUi
import com.mobilejazz.harmony.kotlin.core.interactor.GetInteractor
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
import javax.inject.Inject

class HomeActivity : AppCompatActivity() {

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

  private fun reloadData(pullToRefresh: Boolean) {
    activity_home_swipe_refresh_srl.isRefreshing = true

    getAskStoriesInteractor(KeyQuery("ask-stories"), if (pullToRefresh) MainSyncOperation else CacheSyncOperation).onCompleteUi(onFailure = {
      // nothing to do
      Snackbar.make(activity_home_items_rv, "Error : " + it.localizedMessage, Snackbar.LENGTH_SHORT)
      Log.e("Error", it.localizedMessage)
    }, onSuccess = {
      getItemsByIdInteractor(it.ids).onCompleteUi(onSuccess = {
        adapter.reloadData(it)

        activity_home_swipe_refresh_srl.isRefreshing = false
      }, onFailure = {
        // nothing to do
        Snackbar.make(activity_home_items_rv, "Error : " + it.localizedMessage, Snackbar.LENGTH_LONG).show()
        Log.e("Error", it.localizedMessage)
      })
    })
  }
}
