package io.github.hiiragi283.api.storage

import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage

@Suppress("UnstableApiUsage")
interface SlottedStorage<T, S : SingleSlotStorage<T>> : HTExtendedStorage<T> {
    val slotCount: Int

    val slots: List<S>
        get() = makeListView(this)

    fun getSlot(slot: Int): S

    companion object {
        private fun <T, S : SingleSlotStorage<T>> makeListView(storage: SlottedStorage<T, S>): List<S> = object : AbstractList<S>() {
            override val size: Int
                get() = storage.slotCount

            override fun get(index: Int): S = storage.getSlot(index)
        }
    }
}
