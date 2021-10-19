package com.harmony.kotlin.domain.exception

open class DomainException(override val message: String? = null, override val cause: Throwable? = null) : Exception(message)
