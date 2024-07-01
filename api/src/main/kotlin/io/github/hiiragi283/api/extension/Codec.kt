package io.github.hiiragi283.api.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps

fun <A : Any, T : Any> Codec<A>.encodeResult(dynamicOps: DynamicOps<T>, input: A): Result<T> =
    runCatchAndLog { encodeStart(dynamicOps, input).result().get() }

fun <A : Any, T : Any> Codec<A>.decodeResult(dynamicOps: DynamicOps<T>, input: T): Result<A> =
    runCatchAndLog { parse(dynamicOps, input).result().get() }

fun <T : Enum<T>> enumCodec(to: (String) -> T): Codec<T> = Codec.STRING.xmap(to) { it.name }
