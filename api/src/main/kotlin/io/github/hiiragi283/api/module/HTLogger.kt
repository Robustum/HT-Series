package io.github.hiiragi283.api.module

import io.github.hiiragi283.api.extension.isDevEnv
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object HTLogger {
    private val logger: Logger = LogManager.getLogger()

    fun log(action: (Logger) -> Unit) {
        action(logger)
    }

    fun debug(action: (Logger) -> Unit) {
        if (isDevEnv) action(logger)
    }
}
