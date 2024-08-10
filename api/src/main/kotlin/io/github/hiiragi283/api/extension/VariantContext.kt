package io.github.hiiragi283.api.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.network.HTPacketCodec
import io.github.hiiragi283.api.storage.HTSingleVariantStorage
import io.github.hiiragi283.api.storage.HTStorageIO
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.registry.Registry

@Suppress("UnstableApiUsage")
sealed class VariantContext<O : Any, T : TransferVariant<O>> private constructor(
    val key: String,
    val codec: Codec<T>,
    val packetCodec: HTPacketCodec<T>,
    val blank: () -> T,
    val simple: (O) -> T,
    val full: (O, NbtCompound) -> T,
    val storage: (HTStorageIO, Long) -> HTSingleVariantStorage<T>,
) {
    data object ITEM : VariantContext<Item, ItemVariant>(
        "item",
        RecordCodecBuilder.create { instance ->
            instance.group(
                Registry.ITEM.fieldOf("item").forGetter(ItemVariant::getItem),
                NbtCompound.CODEC.orElse(NbtCompound()).fieldOf("nbt").forGetter(ItemVariant::getNbt),
            ).apply(instance, ItemVariant::of)
        },
        HTPacketCodec.createSimple(
            HTModuleType.API.id("variant/item"),
            ItemVariant::toPacket,
            ItemVariant::fromPacket,
        ),
        ItemVariant::blank,
        ItemVariant::of,
        ItemVariant::of,
        HTSingleVariantStorage.Companion::ofItem,
    )

    data object FLUID : VariantContext<Fluid, FluidVariant>(
        "fluid",
        RecordCodecBuilder.create { instance ->
            instance.group(
                Registry.FLUID.fieldOf("fluid").forGetter(FluidVariant::getFluid),
                NbtCompound.CODEC.orElse(NbtCompound()).fieldOf("nbt").forGetter(FluidVariant::getNbt),
            ).apply(instance, FluidVariant::of)
        },
        HTPacketCodec.createSimple(
            HTModuleType.API.id("variant/fluid"),
            FluidVariant::toPacket,
            FluidVariant::fromPacket,
        ),
        FluidVariant::blank,
        FluidVariant::of,
        FluidVariant::of,
        HTSingleVariantStorage.Companion::ofFluid,
    )
}
