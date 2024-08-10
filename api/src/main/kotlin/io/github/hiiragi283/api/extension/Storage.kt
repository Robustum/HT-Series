@file:Suppress("UnstableApiUsage")

package io.github.hiiragi283.api.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.storage.HTStorageIO
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtOps

fun Storage<*>.getIOType() = when {
    supportsInsertion() && supportsExtraction() -> HTStorageIO.GENERIC
    supportsInsertion() -> HTStorageIO.INPUT
    supportsExtraction() -> HTStorageIO.OUTPUT
    else -> HTStorageIO.INTERNAL
}

fun <T : TransferVariant<*>> resourceAmountCodec(codec: Codec<T>): Codec<ResourceAmount<T>> = RecordCodecBuilder.create { instance ->
    instance.group(
        codec.fieldOf("resource").forGetter(ResourceAmount<T>::resource),
        Codec.LONG.fieldOf("amount").forGetter(ResourceAmount<T>::amount),
    ).apply(instance, ::ResourceAmount)
}

fun <T : TransferVariant<*>> SingleVariantStorage<T>.readNbt(codec: Codec<T>, fallback: () -> T, nbt: NbtCompound) {
    this.variant = codec.decodeResult(NbtOps.INSTANCE, nbt.getCompound("variant"))
        .onFailure {
            HTLogger.debug { it.warn("Failed to load an ItemVariant from NBT: {}", it) }
        }
        .getOrDefault(fallback())
    this.amount = nbt.getLong("amount")
}

fun <T : TransferVariant<*>> SingleVariantStorage<T>.writeNbt(codec: Codec<T>, nbt: NbtCompound) {
    nbt.put("variant", codec.encodeResult(NbtOps.INSTANCE, this.variant).getOrThrow())
    nbt.putLong("amount", this.amount)
}
