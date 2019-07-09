package com.mobilejazz.sample.core.data.mapper

import com.mobilejazz.harmony.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.sample.core.data.model.ItemEntity
import com.mobilejazz.sample.core.data.model.ItemIdsEntity
import com.mobilejazz.sample.core.domain.model.Item
import com.mobilejazz.sample.core.domain.model.ItemIds

class ItemEntityToItemMapper : Mapper<ItemEntity, Item> {

  override fun map(from: ItemEntity): Item = Item(from.id, from.by ?: "by: unknown", from.title, from.text, from.kids)

}

class ItemIdsEntityToItemIdsMapper : Mapper<ItemIdsEntity, ItemIds> {
  override fun map(from: ItemIdsEntity): ItemIds = ItemIds(from.ids)
}

class ItemIdsToItemIdsEntityMapper : Mapper<ItemIds, ItemIdsEntity> {
  override fun map(from: ItemIds): ItemIdsEntity = ItemIdsEntity(from.ids)
}