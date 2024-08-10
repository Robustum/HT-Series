package io.github.hiiragi283.api.block.entity

import io.github.hiiragi283.api.storage.HTSlottedStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.util.math.Direction

@Suppress("UnstableApiUsage")
interface HTStorageProvider {
    fun getItemStorage(side: Direction?): HTSlottedStorage<Item, ItemVariant>? = null

    fun getFluidStorage(side: Direction?): HTSlottedStorage<Fluid, FluidVariant>? = null
}
