package io.github.hiiragi283.api.extension

import net.minecraft.util.collection.DefaultedList

inline fun <T : Any> buildDefaultedList(size: Int, defaultValue: T, builderAction: DefaultedList<T>.() -> Unit): DefaultedList<T> =
    DefaultedList.ofSize(size, defaultValue).apply(builderAction)
