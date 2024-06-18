package io.github.hiiragi283.api.storage

import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext

@Suppress("UnstableApiUsage")
interface HTExtendedStorage<T> : Storage<T>, Iterable<StorageView<T>> {
    override fun iterator(transaction: TransactionContext): Iterator<StorageView<T>> = iterator()
}
