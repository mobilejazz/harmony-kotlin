package com.mobilejazz.kotlin.core.sample.domain.model

import java.math.BigDecimal


data class Item(val id: String?,
                val name: String,
                val price: BigDecimal,
                val count: Int,
                val imageUrl: String)