package com.harmony.kotlin.data.utilities

import com.harmony.kotlin.common.randomString
import com.harmony.kotlin.data.query.anyKeyQuery

fun anyInsertionValue() = InsertionValue(anyKeyQuery(randomString()), randomString())
