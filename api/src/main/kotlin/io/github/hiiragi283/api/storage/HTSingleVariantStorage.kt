package io.github.hiiragi283.api.storage

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage

@Suppress("UnstableApiUsage")
abstract class HTSingleVariantStorage<T : TransferVariant<*>>(
    private val ioType: HTStorageIO,
    private val capacity: Long,
) : SingleVariantStorage<T>() {
    override fun getCapacity(variant: T): Long = capacity

    override fun supportsInsertion(): Boolean = ioType.canInsert

    override fun supportsExtraction(): Boolean = ioType.canExtract

    class Item(ioType: HTStorageIO, capacity: Long) : HTSingleVariantStorage<ItemVariant>(ioType, capacity) {
        override fun getBlankVariant(): ItemVariant = ItemVariant.blank()
    }

    class Fluid(ioType: HTStorageIO, capacity: Long) : HTSingleVariantStorage<FluidVariant>(ioType, capacity) {
        override fun getBlankVariant(): FluidVariant = FluidVariant.blank()
    }

    /*class Material(ioType: HTStorageIO, capacity: Long) : HTSingleVariantStorage<HTMaterialVariant>(ioType, capacity) {
        override fun getBlankVariant(): HTMaterialVariant = HTMaterialVariant.blank()
    }*/
}
