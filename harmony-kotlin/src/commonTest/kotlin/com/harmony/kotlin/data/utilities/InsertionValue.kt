package com.harmony.kotlin.data.utilities

import com.harmony.kotlin.data.query.KeyQuery

data class InsertionValue<T>(val query: KeyQuery, val value: T?)
