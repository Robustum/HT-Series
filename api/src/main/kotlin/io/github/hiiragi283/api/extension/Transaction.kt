package io.github.hiiragi283.api.extension

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction

@Suppress("UnstableApiUsage")
inline fun useOuterTransaction(action: (Transaction) -> Unit): Result<Unit> = runCatching {
    Transaction.openOuter().use(action)
}
