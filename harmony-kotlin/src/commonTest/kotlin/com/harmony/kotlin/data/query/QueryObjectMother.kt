package com.harmony.kotlin.data.query

import com.harmony.kotlin.common.randomString

fun anyQuery() = object : Query {}

fun anyVoidQuery() = VoidQuery

fun anyKeyQuery(key: String = randomString()) = KeyQuery(key)
