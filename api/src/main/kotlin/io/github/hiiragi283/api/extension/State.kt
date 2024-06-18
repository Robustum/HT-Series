package io.github.hiiragi283.api.extension

import net.minecraft.state.State
import net.minecraft.state.property.Property
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

fun <T : Comparable<T>> State<*, *>.getOrDefault(property: Property<T>, defaultValue: T): T =
    getOrEmpty(property).getOrDefault(defaultValue)

fun <T : Comparable<T>> State<*, *>.getOrNull(property: Property<T>): T? = getOrEmpty(property).getOrNull()
