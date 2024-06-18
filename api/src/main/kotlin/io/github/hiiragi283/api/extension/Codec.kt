package io.github.hiiragi283.api.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps

fun <A : Any, T : Any> Codec<A>.encodeResult(dynamicOps: DynamicOps<T>, input: A): Result<T> =
    runCatching { encodeStart(dynamicOps, input).result().get() }

fun <A : Any, T : Any> Codec<A>.decodeResult(dynamicOps: DynamicOps<T>, input: T): Result<A> =
    runCatching { parse(dynamicOps, input).result().get() }
