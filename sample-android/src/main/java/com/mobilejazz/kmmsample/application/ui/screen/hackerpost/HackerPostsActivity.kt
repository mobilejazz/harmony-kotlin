package com.mobilejazz.kmmsample.application.ui.screen.hackerpost

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobilejazz.kmmsample.application.HarmonySampleApp
import com.mobilejazz.kmmsample.application.R
import com.mobilejazz.kmmsample.application.databinding.ActivityHackerPostsBinding
import com.mobilejazz.kmmsample.application.ui.common.toLocalizedErrorMessage
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPosts
import com.mobilejazz.kmmsample.core.screen.mvp.hackerPosts.HackerPostsPresenter

class HackerPostsActivity : AppCompatActivity(), HackerPostsPresenter.View {

  private lateinit var binding: ActivityHackerPostsBinding
  private val presenter by lazy {
    HarmonySampleApp.appProvider.presenterComponent.getHackerPostsPresenter(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityHackerPostsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    presenter.onViewLoaded()

    title = "Hacker News"
  }

  override fun onDisplayLoading() {
    binding.loadContentLayout.showLoading()
  }

  override fun onDisplayHackerPostList(hackerNewsPosts: HackerNewsPosts) {
    binding.loadContentLayout.showContent(true)
    val hackerPostsAdapter = HackerPostsAdapter(hackerNewsPosts) { hackerNewsPost ->
      val intent = Intent(this@HackerPostsActivity, HackerPostDetailActivity::class.java)
      intent.putExtra(HackerPostDetailActivity.HACKER_POST_ID_EXTRA, hackerNewsPost.id)
      startActivity(intent)
    }
    binding.hackerPostRecyclerView.adapter = hackerPostsAdapter
  }

  override fun onFailedWithFullScreenError(t: Throwable, retryBlock: () -> Unit) {
    binding.loadContentLayout.showError(t.toLocalizedErrorMessage(this), R.string.ls_retry, retryBlock)
  }
}
