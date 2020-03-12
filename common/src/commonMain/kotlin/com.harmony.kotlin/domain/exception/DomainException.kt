package com.harmony.kotlin.domain.exception

open class DomainException(override val message: String?, override val cause: Throwable?) : Exception(message)
