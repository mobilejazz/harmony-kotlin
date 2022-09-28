package com.harmony.kotlin.common.logger

import io.ktor.client.plugins.logging.Logger

class KtorHarmonyLogger(val logger: com.harmony.kotlin.common.logger.Logger) : Logger {
  override fun log(message: String) {
    logger.d(message)
  }
}
