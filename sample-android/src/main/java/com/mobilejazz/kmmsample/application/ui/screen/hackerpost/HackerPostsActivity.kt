package com.mobilejazz.kmmsample.application.ui.screen.hackerpost

import android.os.Bundle
import android.widget.Toast
import com.mobilejazz.kmmsample.application.HarmonySampleApp
import com.mobilejazz.kmmsample.application.R
import com.mobilejazz.kmmsample.application.databinding.ActivityHackerPostsBinding
import com.mobilejazz.kmmsample.application.ui.common.BaseActivity
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPosts
import com.mobilejazz.kmmsample.core.screen.hackerposts.HackerPostsPresenter

class HackerPostsActivity : BaseActivity(), HackerPostsPresenter.View {

  private lateinit var binding: ActivityHackerPostsBinding
  private val presenter by lazy {
    HarmonySampleApp.appProvider.presenterComponent.getHackerPostsPresenter(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityHackerPostsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    presenter.onViewLoaded()
  }

  override fun onDisplayHackerPostList(hackerNewsPosts: HackerNewsPosts) {
    val hackerPostsAdapter = HackerPostsAdapter(hackerNewsPosts)
    binding.hackerPostRecyclerView.adapter = hackerPostsAdapter
  }

  override fun onFailedLoadHackerPostList(e: Exception) {
    Toast.makeText(this, getString(R.string.error_load_hacker_posts), Toast.LENGTH_LONG).show()
  }

  override fun onStop() {
    super.onStop()
    presenter.onDetachView()
  }
}