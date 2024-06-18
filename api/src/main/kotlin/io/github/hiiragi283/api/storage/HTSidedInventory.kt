package io.github.hiiragi283.api.storage

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction

class HTSidedInventory(val builder: HTSidedStorageBuilder) : SidedInventory {
    private val stacks: DefaultedList<ItemStack> = DefaultedList.ofSize(builder.ioTypes.size, ItemStack.EMPTY)

    fun writeNbt(nbt: NbtCompound) {
        Inventories.writeNbt(nbt, stacks)
    }

    fun readNbt(nbt: NbtCompound) {
        Inventories.readNbt(nbt, stacks)
    }

    //    SidedInventory    //

    override fun clear() {
        stacks.clear()
    }

    override fun size(): Int = stacks.size

    override fun isEmpty(): Boolean = stacks.isEmpty()

    override fun getStack(slot: Int): ItemStack = stacks.getOrNull(slot) ?: ItemStack.EMPTY

    override fun removeStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(stacks, slot, amount)

    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(stacks, slot)

    override fun setStack(slot: Int, stack: ItemStack) {
        stacks[slot] = stack
    }

    override fun markDirty() = Unit

    override fun canPlayerUse(player: PlayerEntity?): Boolean = true

    override fun getAvailableSlots(side: Direction): IntArray = builder.getSideSlotArray(side)

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean =
        dir?.let { builder.ioTypes[slot].canInsert && slot in builder.getSideSlots(it) } ?: false

    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction): Boolean =
        builder.ioTypes[slot].canExtract && slot in builder.getSideSlots(dir)
}
