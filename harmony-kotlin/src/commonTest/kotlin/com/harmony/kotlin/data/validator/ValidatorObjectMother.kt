package com.harmony.kotlin.data.validator

import com.harmony.kotlin.data.validator.mock.MockValidator

fun <T> anyMockValidator(validatorResponse: Boolean = true) = MockValidator<T>(forceValidationResponse = validatorResponse)
