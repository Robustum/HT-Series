package io.github.hiiragi283.api.extension

import java.util.*

val Enum<*>.lowerName: String
    get() = this.name.lowercase(Locale.getDefault())
