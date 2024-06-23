package io.github.hiiragi283.api.extension

inline fun <T> T.check(predicate: T.() -> Boolean, message: () -> Any): T = apply { check(predicate(this), message) }

fun <T> T?.checkNotNull(message: () -> Any): T = checkNotNull(this, message)
