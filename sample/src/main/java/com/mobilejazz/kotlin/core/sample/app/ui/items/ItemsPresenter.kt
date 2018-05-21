package com.mobilejazz.kotlin.core.sample.app.ui.items

import com.mobilejazz.kotlin.core.domain.interactor.GetAllInteractor
import com.mobilejazz.kotlin.core.repository.operation.StorageSyncOperation
import com.mobilejazz.kotlin.core.repository.query.StringKeyQuery
import com.mobilejazz.kotlin.core.sample.app.di.ActivityScope
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.threading.extensions.onCompleteUi
import com.mobilejazz.kotlin.core.ui.base.presenter.BasePresenter
import com.mobilejazz.kotlin.core.ui.base.view.MVPView
import javax.inject.Inject

@ActivityScope
class ItemsPresenter @Inject constructor(
    private val getItemsInteractor: GetAllInteractor<Item>
) : BasePresenter<ItemsPresenter.View>() {
  override fun onCreate(bundle: Map<String, Any>) {
    loadItems()
  }

  override fun onResume() {
  }

  override fun onPause() {
  }

  override fun onDestroy() {
  }


  fun loadItems() {
    view?.onDisplayLoading()

    // TODO @Fran @Jose What happen if I don't use StringKeyQuery???
    getItemsInteractor(operation = StorageSyncOperation, query = StringKeyQuery("items")).onCompleteUi(
        onSuccess = {
          view?.onDisplayItems(it)
        },
        onFailure = {
          view?.onDisplayError(it)
        })
  }

  interface View : MVPView {
    fun onDisplayLoading()
    fun onDisplayItems(items: List<Item>)
  }


}