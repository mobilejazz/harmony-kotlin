package com.mobilejazz.kotlin.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.mobilejazz.kotlin.core.R

class LoadContentLayout : CoordinatorLayout {

  private val lazyLoadingView = lazy {
    val loadingView = LayoutInflater.from(context).inflate(R.layout.layout_loading, this, false)
    super.addView(loadingView)
    loadingView
  }
  private val lazyErrorView = lazy {
    val errorView = LayoutInflater.from(context).inflate(R.layout.layout_error, this, false)
    addView(errorView)
    errorView
  }
  private val loadingView: View by lazyLoadingView
  private val errorView: View by lazyErrorView

  private lateinit var contentView: View

  private var animIn: Animation? = null
  private var animOut: Animation? = null

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    init()
  }

  private fun init() {
    animIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    animOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    if (childCount > 1) {
      throw IllegalStateException("This view allow only one direct child")
    }
    contentView = getChildAt(0)
  }

  fun showLoading() {
    hideError()
    hideContent()
    if (loadingView.visibility != View.VISIBLE) {
      loadingView.startAnimation(animIn)
      loadingView.visibility = View.VISIBLE
    }
  }

  private fun hideLoading() {
    if (lazyLoadingView.isInitialized() && loadingView.visibility == View.VISIBLE) {
      loadingView.startAnimation(animOut)
      loadingView.visibility = View.GONE
    }
  }

  fun showContent(animate: Boolean) {
    if (contentView.visibility != View.VISIBLE) {
      hideLoading()
      hideError()
      if (animate) {
        contentView.startAnimation(animIn)
      }
      contentView.visibility = View.VISIBLE
    }
  }

  private fun hideContent() {
    if (contentView.visibility == View.VISIBLE) {
      contentView.visibility = View.GONE
    }
  }

  @JvmOverloads
  fun showError(errorMessage: String, handleErrorButtonMessageResId: Int = 0, errorHandler: () -> Unit = {}) {
    val txError = errorView.findViewById<TextView>(R.id.error_message_tv)
    txError.text = errorMessage

    val btError = errorView.findViewById<Button>(R.id.error_action_btn)
    if (handleErrorButtonMessageResId != 0) {
      btError.setText(handleErrorButtonMessageResId)
      btError.setOnClickListener { errorHandler() }
      btError.visibility = View.VISIBLE
    } else {
      btError.visibility = View.GONE
    }
    showError()
  }

  private fun showError() {
    hideLoading()
    hideContent()
    if (errorView.visibility != View.VISIBLE) {
      errorView.startAnimation(animIn)
      errorView.visibility = View.VISIBLE
    }
  }

  private fun hideError() {
    if (lazyErrorView.isInitialized() && errorView.visibility == View.VISIBLE) {
      errorView.startAnimation(animOut)
      errorView.visibility = View.GONE
    }
  }
}
