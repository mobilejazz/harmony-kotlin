package com.mobilejazz.kotlin.core.sample.domain.model

import java.math.BigDecimal

data class Item(val id: String?,
                val name: String,
                val price: Int,
                val count: Int,
                val imageUrl: String){

  override fun toString(): String {
    return "Item(name='$name')"
  }
}