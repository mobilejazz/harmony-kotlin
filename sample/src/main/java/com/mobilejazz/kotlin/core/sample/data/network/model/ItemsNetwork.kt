package com.mobilejazz.kotlin.core.sample.data.network.model

import com.mobilejazz.kotlin.core.sample.domain.model.Item
import com.mobilejazz.kotlin.core.sample.repository.entity.ItemEntity

data class ItemsNetwork(val results: List<ItemEntity>)

