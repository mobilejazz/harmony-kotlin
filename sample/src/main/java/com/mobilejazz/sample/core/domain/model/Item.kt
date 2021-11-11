package com.mobilejazz.sample.core.domain.model

import java.io.Serializable

data class Item(val id: Int, val by: String, val title: String?, val text: String?, val kids: List<Int>?) : Serializable
