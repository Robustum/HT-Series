@file:Suppress("UnstableApiUsage")

package io.github.hiiragi283.api.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.hiiragi283.api.storage.HTStorageIO
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount
import net.minecraft.nbt.NbtCompound

fun Storage<*>.getIOType() = when {
    supportsInsertion() && supportsExtraction() -> HTStorageIO.GENERIC
    supportsInsertion() -> HTStorageIO.INPUT
    supportsExtraction() -> HTStorageIO.OUTPUT
    else -> HTStorageIO.INTERNAL
}

fun <T : TransferVariant<*>> resourceAmountCodec(fromNbt: (NbtCompound) -> T): Codec<ResourceAmount<T>> =
    RecordCodecBuilder.create { instance ->
        instance.group(
            NbtCompound.CODEC.fieldOf("resource").forGetter { it.resource().toNbt() },
            Codec.LONG.fieldOf("amount").forGetter { it.amount() },
        ).apply(instance) { resource, amount -> ResourceAmount(fromNbt(resource), amount) }
    }
