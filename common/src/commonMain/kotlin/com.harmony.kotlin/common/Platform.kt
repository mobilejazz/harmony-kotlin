package com.harmony.kotlin.common

expect object Platform {
    actual val IS_JVM: Boolean
    actual val IS_NATIVE: Boolean
}
