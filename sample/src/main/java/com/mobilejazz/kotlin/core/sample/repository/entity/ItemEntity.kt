package com.mobilejazz.kotlin.core.sample.repository.entity

import com.google.gson.annotations.SerializedName

data class ItemEntity(val id: String?,
                      val name: String,
                      val price: Double,
                      val count: Int,
                      @SerializedName("image-url")
                      val imageUrl: String){

    override fun toString(): String {
        return "Item(name='$name')"
    }
}

