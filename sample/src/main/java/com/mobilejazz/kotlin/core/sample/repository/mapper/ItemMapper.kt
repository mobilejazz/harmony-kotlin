package com.mobilejazz.kotlin.core.sample.repository.mapper

import com.mobilejazz.kotlin.core.repository.mapper.Mapper
import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.sample.repository.entity.ItemEntity

class ItemToItemEntityMapper : Mapper<Item, ItemEntity> {
    override fun map(from: Item): ItemEntity = ItemEntity(from.id, from.name, from.price, from.count, from.imageUrl)
}

class ItemEntityToItemMapper: Mapper<ItemEntity, Item> {
    override fun map(from: ItemEntity): Item = Item(from.id, from.name, from.price, from.count, from.imageUrl)
}