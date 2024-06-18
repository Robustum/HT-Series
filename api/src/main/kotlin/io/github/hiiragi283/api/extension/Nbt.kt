package io.github.hiiragi283.api.extension

import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.util.Identifier

//    NbtCompound    //

fun buildNbt(builderAction: NbtCompound.() -> Unit): NbtCompound = NbtCompound().apply(builderAction)

fun NbtCompound.putIdentifier(key: String, value: Identifier) = putString(key, value.toString())

fun <V : Any> Map<String, V>.toNbtCompound(mapping: (V) -> NbtElement): NbtCompound = buildNbt {
    this@toNbtCompound.mapValues { mapping(it.value) }.forEach { (key, json) -> put(key, json) }
}

fun <K : Any, V : Any> Map<K, V>.toNbtCompound(keyMapping: (K) -> String, valueMapping: (V) -> NbtElement): NbtCompound =
    this@toNbtCompound.mapKeys { keyMapping(it.key) }.toNbtCompound(valueMapping)

//    NbtList    //

fun buildNbtList(builderAction: NbtList.() -> Unit): NbtList = NbtList().apply(builderAction)

fun NbtList.add(value: Identifier) = add(NbtString.of(value.toString()))

fun Iterable<NbtElement>.toNbtList() = toNbtList { it }

fun <T> Iterable<T>.toNbtList(mapping: (T) -> NbtElement): NbtList = buildNbtList {
    this@toNbtList.map(mapping).forEach(::add)
}
