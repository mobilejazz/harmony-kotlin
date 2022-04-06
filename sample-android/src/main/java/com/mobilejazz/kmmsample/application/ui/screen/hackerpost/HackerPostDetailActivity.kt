package com.mobilejazz.kmmsample.application.ui.screen.hackerpost

import android.os.Bundle
import android.view.MenuItem
import com.mobilejazz.kmmsample.application.HarmonySampleApp
import com.mobilejazz.kmmsample.application.R
import com.mobilejazz.kmmsample.application.databinding.ActivityHackerPostDetailBinding
import com.mobilejazz.kmmsample.application.ui.common.BaseActivity
import com.mobilejazz.kmmsample.application.ui.common.toLocalizedErrorMessage
import com.mobilejazz.kmmsample.core.feature.hackerposts.domain.model.HackerNewsPost
import com.mobilejazz.kmmsample.core.screen.hackerposts.HackerPostDetailPresenter
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class HackerPostDetailActivity : BaseActivity(), HackerPostDetailPresenter.View {

  companion object {
    const val HACKER_POST_ID_EXTRA = "HACKER_POST_ID"
  }

  private lateinit var binding: ActivityHackerPostDetailBinding
  private val presenter by lazy {
    HarmonySampleApp.appProvider.presenterComponent.getHackerPostDetailPresenter(this)
  }

  private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityHackerPostDetailBinding.inflate(layoutInflater)
    setContentView(binding.root)

    supportActionBar?.setDisplayHomeAsUpEnabled(true);
    supportActionBar?.setDisplayShowHomeEnabled(true);

    title = "Hacker News Post"

    val hackerNewsPostId = intent.getIntExtra(HACKER_POST_ID_EXTRA, -1)
    presenter.onViewLoaded(hackerNewsPostId)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onDisplayLoading() {
    binding.loadContentLayout.showLoading()
  }

  override fun onDisplayHackerPost(hackerNewsPost: HackerNewsPost) {
    binding.loadContentLayout.showContent(true)
    with(hackerNewsPost) {
      binding.tvTimeDetail.text = formatter.format(hackerNewsPost.time.toJavaLocalDateTime())
      binding.tvTitleDetail.text = hackerNewsPost.title
      binding.tvAuthorDetail.text = getString(R.string.ls_author, hackerNewsPost.by)
    }
  }

  override fun onFailedWithFullScreenError(t: Throwable, retryBlock: () -> Unit) {
    binding.loadContentLayout.showError(t.toLocalizedErrorMessage(this), R.string.ls_retry, retryBlock)
  }

}
