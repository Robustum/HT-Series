package io.github.hiiragi283.api.extension

import net.minecraft.state.State
import net.minecraft.state.property.Property

fun <T : Comparable<T>> State<*, *>.tryGet(property: Property<T>): Result<T> = runCatchAndLog { get(property) }

fun <T : Comparable<T>> State<*, *>.getOrNull(property: Property<T>): T? = tryGet(property).getOrNull()
