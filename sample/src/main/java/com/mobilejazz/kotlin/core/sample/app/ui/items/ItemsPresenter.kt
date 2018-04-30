package com.mobilejazz.kotlin.core.sample.app.ui.items

import com.mobilejazz.kotlin.core.sample.app.di.ActivityScope
import com.mobilejazz.kotlin.core.sample.domain.items.GetItemsInteractor
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.threading.extensions.onCompleteUi
import com.mobilejazz.kotlin.core.ui.base.presenter.BasePresenter
import com.mobilejazz.kotlin.core.ui.base.view.MVPView
import javax.inject.Inject

@ActivityScope
class ItemsPresenter @Inject constructor(
    private val getItemsInteractor: GetItemsInteractor
) : BasePresenter<ItemsPresenter.View>() {
  override fun onCreate(bundle: Map<String, Any>) {
    getItemsInteractor().onCompleteUi(
        onSuccess = {
          view?.onDisplayItems(it)
        },
        onFailure = {
          // TODO error handling
        })
  }

  override fun onResume() {
  }

  override fun onPause() {
  }

  override fun onDestroy() {
  }

  interface View : MVPView {
    fun onDisplayItems(items: List<Item>)

  }


}