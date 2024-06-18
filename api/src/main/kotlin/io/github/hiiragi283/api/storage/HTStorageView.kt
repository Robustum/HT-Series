package io.github.hiiragi283.api.storage

import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import kotlin.math.max

@Suppress("UnstableApiUsage")
class HTStorageView<T : TransferVariant<*>>(
    @JvmField val resource: T,
    @JvmField val amount: Long,
) : StorageView<T> {
    constructor(pair: Pair<T, Long>) : this(pair.first, pair.second)

    constructor(entry: Map.Entry<T, Long>) : this(entry.key, entry.value)

    override fun extract(resource: T, maxAmount: Long, transaction: TransactionContext): Long =
        if (this.resource == resource) max(maxAmount, amount) else 0

    override fun isResourceBlank(): Boolean = resource.isBlank

    override fun getResource(): T = resource

    override fun getAmount(): Long = amount

    override fun getCapacity(): Long = amount
}
