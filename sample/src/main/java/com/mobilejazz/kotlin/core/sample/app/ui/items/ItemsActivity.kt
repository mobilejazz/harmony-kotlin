package com.mobilejazz.kotlin.core.sample.app.ui.items

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.mobilejazz.kotlin.core.domain.interactor.GetAllInteractor
import com.mobilejazz.kotlin.core.repository.*
import com.mobilejazz.kotlin.core.repository.datasource.DeleteDataSource
import com.mobilejazz.kotlin.core.repository.datasource.GetDataSource
import com.mobilejazz.kotlin.core.repository.datasource.PutDataSource
import com.mobilejazz.kotlin.core.repository.datasource.memory.InMemoryDataSource
import com.mobilejazz.kotlin.core.repository.operation.StorageSyncOperation
import com.mobilejazz.kotlin.core.repository.query.StringKeyQuery
import com.mobilejazz.kotlin.core.sample.R
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.sample.repository.entity.ItemEntity
import com.mobilejazz.kotlin.core.sample.repository.mapper.ItemEntityToItemMapper
import com.mobilejazz.kotlin.core.sample.repository.mapper.ItemToItemEntityMapper
import com.mobilejazz.kotlin.core.sample.repository.network.ItemNetworkDataSource
import com.mobilejazz.kotlin.core.threading.AppExecutor
import com.mobilejazz.kotlin.core.threading.extensions.onCompleteUi
import com.mobilejazz.kotlin.core.ui.base.view.BaseMVPActivity
import dagger.multibindings.StringKey
import kotlinx.android.synthetic.main.activity_items.*


fun main(args: Array<String>) {
    val itemNetworkDataSource = ItemNetworkDataSource()

    val getNetworkDatasource: GetDataSource<ItemEntity> = itemNetworkDataSource
    val putNetworkDataSource: PutDataSource<ItemEntity> = itemNetworkDataSource
    val deleteNetworkDataSource: DeleteDataSource = itemNetworkDataSource

    val itemStorageDataSource = InMemoryDataSource<ItemEntity>()

    val getStorageDataSource: GetDataSource<ItemEntity> = itemStorageDataSource
    val putStorageDataSource: PutDataSource<ItemEntity> = itemStorageDataSource
    val deleteStorageDataSource: DeleteDataSource = itemStorageDataSource

    val itemNetworkStorageRepository = NetworkStorageRepository(getStorageDataSource, putStorageDataSource, deleteStorageDataSource, getNetworkDatasource, putNetworkDataSource, deleteNetworkDataSource)

    val toItemEntityMapper = ItemToItemEntityMapper()
    val toItemMapper = ItemEntityToItemMapper()

    val itemRepositoryMapper = RepositoryMapper(itemNetworkStorageRepository, itemNetworkStorageRepository, itemNetworkStorageRepository, toItemEntityMapper, toItemMapper)

    val getRepository: GetRepository<Item> = itemRepositoryMapper
    val putRepository: PutRepository<Item> = itemRepositoryMapper
    val deleteRepository: DeleteRepository = itemRepositoryMapper


    val getAllItem = GetAllInteractor(AppExecutor, getRepository)

    getAllItem(operation = StorageSyncOperation).onCompleteUi(onSuccess = {
        Log.d("TEST", it.toString())
    }, onFailure = {
        Log.d("TEST", "Error")
    })
}

class ItemsActivity : BaseMVPActivity<ItemsPresenter, ItemsPresenter.View>(), ItemsPresenter.View {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

//        val itemNetworkDataSource = ItemNetworkDataSource()
//
//        val getNetworkDatasource: GetDataSource<ItemEntity> = itemNetworkDataSource
//        val putNetworkDataSource: PutDataSource<ItemEntity> = itemNetworkDataSource
//        val deleteNetworkDataSource: DeleteDataSource = itemNetworkDataSource
//
//        val itemStorageDataSource = InMemoryDataSource<ItemEntity>()
//
//        val getStorageDataSource: GetDataSource<ItemEntity> = itemStorageDataSource
//        val putStorageDataSource: PutDataSource<ItemEntity> = itemStorageDataSource
//        val deleteStorageDataSource: DeleteDataSource = itemStorageDataSource
//
//        val itemNetworkStorageRepository = NetworkStorageRepository(getStorageDataSource, putStorageDataSource, deleteStorageDataSource, getNetworkDatasource, putNetworkDataSource, deleteNetworkDataSource)
//
//        val toItemEntityMapper = ItemToItemEntityMapper()
//        val toItemMapper = ItemEntityToItemMapper()
//
//        val itemRepositoryMapper = RepositoryMapper(itemNetworkStorageRepository, itemNetworkStorageRepository, itemNetworkStorageRepository, toItemEntityMapper, toItemMapper)
//
//        val getRepository: GetRepository<Item> = itemRepositoryMapper
//        val putRepository: PutRepository<Item> = itemRepositoryMapper
//        val deleteRepository: DeleteRepository = itemRepositoryMapper
//
//
//        val getAllItem = GetAllInteractor(AppExecutor, getRepository)
//
//        getAllItem(query = StringKeyQuery("all-items"), operation = StorageSyncOperation).onCompleteUi(onSuccess = {
//            Log.d("TEST", it.toString())
//        }, onFailure = {
//            Log.d("TEST", "Error")
//        })


    }

    override fun onDisplayItems(items: List<Item>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items.map { it.toString() })
        items_lv.adapter = ItemsAdapter(items)
    }


    override fun onDisplayError(throwable: Throwable) {

    }
}