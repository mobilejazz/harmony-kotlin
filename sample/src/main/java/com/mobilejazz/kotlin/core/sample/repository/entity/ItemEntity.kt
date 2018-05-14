package com.mobilejazz.kotlin.core.sample.repository.entity

data class ItemEntity(val id: String?,
                      val name: String,
                      val price: Int,
                      val count: Int,
                      val imageUrl: String){

    override fun toString(): String {
        return "Item(name='$name')"
    }
}

