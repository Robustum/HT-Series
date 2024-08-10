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

    override fun canInsert(variant: T): Boolean = ioType.canInsert

    override fun canExtract(variant: T): Boolean = ioType.canExtract

    override fun supportsInsertion(): Boolean = ioType.canInsert

    override fun supportsExtraction(): Boolean = ioType.canExtract

    companion object {
        @JvmStatic
        fun ofItem(ioType: HTStorageIO, capacity: Long): HTSingleVariantStorage<ItemVariant> =
            object : HTSingleVariantStorage<ItemVariant>(ioType, capacity) {
                override fun getBlankVariant(): ItemVariant = ItemVariant.blank()
            }

        @JvmStatic
        fun ofFluid(ioType: HTStorageIO, capacity: Long): HTSingleVariantStorage<FluidVariant> =
            object : HTSingleVariantStorage<FluidVariant>(ioType, capacity) {
                override fun getBlankVariant(): FluidVariant = FluidVariant.blank()
            }
    }
}
