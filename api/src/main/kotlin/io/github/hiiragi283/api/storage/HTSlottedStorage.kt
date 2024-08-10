package io.github.hiiragi283.api.storage

import io.github.hiiragi283.api.extension.*
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtList
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction

@Suppress("UnstableApiUsage")
class HTSlottedStorage<O : Any, T : TransferVariant<O>> private constructor(
    override val slots: List<SingleVariantStorage<T>>,
    private val sides: Map<Direction, List<Int>>,
    private val context: VariantContext<O, T>,
) : SlottedStorage<T, SingleVariantStorage<T>> {
    companion object {
        @JvmStatic
        fun <O : Any, T : TransferVariant<O>> of(
            builder: HTSidedStorageBuilder,
            capacity: Long = 64,
            slotBuilder: (HTStorageIO, Long) -> HTSingleVariantStorage<T>,
            context: VariantContext<O, T>,
        ): HTSlottedStorage<O, T> = HTSlottedStorage(builder.ioTypes.map { slotBuilder(it, capacity) }, builder.sides, context)

        @JvmOverloads
        @JvmStatic
        fun ofItem(builder: HTSidedStorageBuilder, capacity: Long = 64): HTSlottedStorage<Item, ItemVariant> =
            of(builder, capacity, HTSingleVariantStorage.Companion::ofItem, VariantContext.ITEM)

        @JvmOverloads
        @JvmStatic
        fun ofFluid(builder: HTSidedStorageBuilder, capacity: Long = FluidConstants.BUCKET * 16): HTSlottedStorage<Fluid, FluidVariant> =
            of(builder, capacity, HTSingleVariantStorage.Companion::ofFluid, VariantContext.FLUID)

        @JvmStatic
        fun <O : Any, T : TransferVariant<O>> wrapSided(
            parent: HTSlottedStorage<O, T>,
            side: Direction?,
            blankSlotStorage: () -> HTSingleVariantStorage<T>,
        ): HTSlottedStorage<O, T> {
            if (side == null) return parent
            val slots: DefaultedList<SingleVariantStorage<T>> = DefaultedList.ofSize(
                parent.slotCount,
                blankSlotStorage(),
            )
            parent.sides.getOrDefault(side, emptyList()).forEach { index: Int ->
                slots[index] = parent.slots[index]
            }
            return HTSlottedStorage(slots, parent.sides, parent.context)
        }

        @JvmStatic
        fun wrapSidedItem(parent: HTSlottedStorage<Item, ItemVariant>, side: Direction?): HTSlottedStorage<Item, ItemVariant> =
            wrapSided(parent, side) {
                HTSingleVariantStorage.ofItem(HTStorageIO.INTERNAL, 0)
            }

        @JvmStatic
        fun wrapSidedFluid(parent: HTSlottedStorage<Fluid, FluidVariant>, side: Direction?): HTSlottedStorage<Fluid, FluidVariant> =
            wrapSided(parent, side) {
                HTSingleVariantStorage.ofFluid(HTStorageIO.INTERNAL, 0)
            }
    }

    fun writeNbt(nbt: NbtCompound) {
        /*slots.forEach { view ->
            HTMaterialsAPI.LOGGER.debug("Stored variant; ${view.resource}, amount; ${view.amount}")
        }*/
        nbt.put(
            context.key,
            buildNbtList {
                slots.forEach { slot ->
                    add(
                        buildNbt {
                            slot.writeNbt(context.codec, this)
                        },
                    )
                }
            },
        )
        /*slots
            .map { ResourceAmount(it.resource, it.amount) }
            .map { resourceAmountCodec(codec).encodeResult(NbtOps.INSTANCE, it) }
            .mapNotNull(Result<NbtElement>::getOrNull)
            .toNbtList()
            .takeIf { it.isNotEmpty() }
            ?.let { nbt.put(key, it) }*/
    }

    fun readNbt(nbt: NbtCompound) {
        val list: NbtList = nbt.getList(context.key, 10)
        if (list.isEmpty()) return
        (0..<list.size).forEach { index: Int ->
            val nbtIn: NbtCompound = list.getCompound(index)
            val slot: SingleVariantStorage<T> = getSlot(index)
            slot.readNbt(context.codec, context.blank, nbtIn)

            /*val resourceAmount: ResourceAmount<T> = resourceAmountCodec(codec).decodeResult(NbtOps.INSTANCE, nbtIn)
                .getOrThrow()
            val resource: T = resourceAmount.resource()
            val amount: Long = resourceAmount.amount()
            useOuterTransaction { transaction ->
                getSlot(index).insert(resource, amount, transaction)
                transaction.commit()
            }*/
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
        for (slot: SingleVariantStorage<T> in slots) {
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
        for (slot: SingleVariantStorage<T> in slots) {
            amount += slot.extract(resource, maxAmount - amount, transaction)
            if (amount == maxAmount) break
        }
        return amount
    }

    //    SlottedStorage    //

    override val slotCount: Int = slots.size

    override fun getSlot(slot: Int): SingleVariantStorage<T> = slots[slot]

    override fun iterator(): Iterator<StorageView<T>> = slots.iterator()
}
