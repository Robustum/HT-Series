package io.github.hiiragi283.api.storage

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.BlankVariantView
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

@Suppress("UnstableApiUsage")
class HTBlankSlotStorage<T : TransferVariant<*>>(blank: T) :
    BlankVariantView<T>(blank, Long.MAX_VALUE),
    SingleSlotStorage<T> {
    override fun insert(resource: T, maxAmount: Long, transaction: TransactionContext): Long = 0
}
