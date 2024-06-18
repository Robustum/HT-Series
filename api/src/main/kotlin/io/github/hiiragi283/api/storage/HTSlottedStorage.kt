package io.github.hiiragi283.api.storage

import io.github.hiiragi283.api.extension.*
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtOps
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction
import java.util.*

@Suppress("UnstableApiUsage")
class HTSlottedStorage<T : TransferVariant<*>> private constructor(
    override val slots: List<SingleSlotStorage<T>>,
    private val sides: Map<Direction, List<Int>>,
    private val key: String,
    private val fromNbt: (NbtCompound) -> T,
) : SlottedStorage<T, SingleSlotStorage<T>> {
    companion object {
        @JvmStatic
        fun <T : TransferVariant<*>> of(
            builder: HTSidedStorageBuilder,
            slotBuilder: (HTStorageIO) -> HTSingleVariantStorage<T>,
            key: String,
            fromNbt: (NbtCompound) -> T,
        ): HTSlottedStorage<T> = HTSlottedStorage(builder.ioTypes.map(slotBuilder), builder.sides, key, fromNbt)

        @JvmOverloads
        @JvmStatic
        fun ofItem(builder: HTSidedStorageBuilder, capacity: Long = 64) =
            of(builder, { HTSingleVariantStorage.Item(it, capacity) }, "ItemStorage", ItemVariant::fromNbt)

        @JvmOverloads
        @JvmStatic
        fun ofFluid(builder: HTSidedStorageBuilder, capacity: Long = FluidConstants.BUCKET * 16) =
            of(builder, { HTSingleVariantStorage.Fluid(it, capacity) }, "FluidStorage", FluidVariant::fromNbt)

        /*@JvmOverloads
        @JvmStatic
        fun ofMaterial(builder: HTSidedStorageBuilder, capacity: Long = FluidConstants.BUCKET * 16) =
            of(
                builder,
                { HTSingleVariantStorage.Material(it, capacity) },
                "MaterialStorage",
                HTMaterialVariant::fromNbt
            )*/

        @JvmStatic
        fun <T : TransferVariant<*>> wrapSided(
            parent: HTSlottedStorage<T>,
            side: Direction?,
            blank: T,
            key: String,
            fromNbt: (NbtCompound) -> T,
        ): HTSlottedStorage<T> {
            if (side == null) return parent
            val slots: DefaultedList<SingleSlotStorage<T>> = DefaultedList.ofSize(
                parent.slotCount,
                HTBlankSlotStorage(blank),
            )
            parent.sides.getOrDefault(side, listOf()).forEach { index: Int ->
                slots[index] = parent.slots[index]
            }
            return HTSlottedStorage(slots, parent.sides, key, fromNbt)
        }

        @JvmStatic
        fun wrapSidedItem(parent: HTSlottedStorage<ItemVariant>, side: Direction?) =
            wrapSided(parent, side, ItemVariant.blank(), "ItemStorage", ItemVariant::fromNbt)

        @JvmStatic
        fun wrapSidedFluid(parent: HTSlottedStorage<FluidVariant>, side: Direction?) =
            wrapSided(parent, side, FluidVariant.blank(), "FluidStorage", FluidVariant::fromNbt)

        /*@JvmStatic
        fun wrapSidedMaterial(parent: HTSlottedStorage<HTMaterialVariant>, side: Direction?) =
            wrapSided(parent, side, HTMaterialVariant.blank(), "MaterialStorage", HTMaterialVariant::fromNbt)*/
    }

    fun writeNbt(nbt: NbtCompound) {
        /*slots.forEach { view ->
            HTMaterialsAPI.LOGGER.debug("Stored variant; ${view.resource}, amount; ${view.amount}")
        }*/
        slots
            .map { ResourceAmount(it.resource, it.amount) }
            .map { resourceAmountCodec(fromNbt).encodeResult(NbtOps.INSTANCE, it) }
            .mapNotNull(Result<NbtElement>::getOrNull)
            .toNbtList()
            .takeIf { it.isNotEmpty() }
            ?.let { nbt.put(key, it) }
    }

    fun readNbt(nbt: NbtCompound, nbtToVariant: (NbtCompound) -> T) {
        val list: NbtList = nbt.getList(key, 10)
        if (list.isEmpty()) return
        (0..<list.size).forEach { index: Int ->
            val nbtIn: NbtCompound = list.getCompound(index)
            val resourceAmount: ResourceAmount<T> = resourceAmountCodec(fromNbt).decodeResult(NbtOps.INSTANCE, nbtIn)
                .getOrThrow()
            val resource: T = resourceAmount.resource()
            val amount: Long = resourceAmount.amount()
            useOuterTransaction { transaction ->
                getSlot(index).insert(resource, amount, transaction)
                transaction.commit()
            }
        }
    }

    //    Storage    //

    override fun supportsInsertion(): Boolean {
        slots.forEach {
            if (it.supportsInsertion()) return true
        }
        return false
    }

    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        var amount = 0L
        for (slot: SingleSlotStorage<T> in slots) {
            amount += slot.insert(resource, maxAmount - amount, transaction)
            if (amount == maxAmount) break
        }
        return amount
    }

    override fun supportsExtraction(): Boolean {
        slots.forEach {
            if (it.supportsExtraction()) return true
        }
        return false
    }

    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount)
        var amount = 0L
        for (slot: SingleSlotStorage<T> in slots) {
            amount += slot.extract(resource, maxAmount - amount, transaction)
            if (amount == maxAmount) break
        }
        return amount
    }

    //    SlottedStorage    //

    override val slotCount: Int = slots.size

    override fun getSlot(slot: Int): SingleSlotStorage<T> = slots[slot]

    override fun iterator(): Iterator<StorageView<T>> = slots.iterator()
}
