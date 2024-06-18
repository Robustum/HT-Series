package io.github.hiiragi283.api.extension

import net.minecraft.tag.Tag

fun <T : Any> Tag<T>.safeValues(): List<T> = try {
    values()
} catch (e: Exception) {
    listOf()
}
