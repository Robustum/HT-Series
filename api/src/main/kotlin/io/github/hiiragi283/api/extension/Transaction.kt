package io.github.hiiragi283.api.extension

import io.github.hiiragi283.api.module.HTLogger
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction

@Suppress("UnstableApiUsage")
inline fun useOuterTransaction(action: (Transaction) -> Unit): Result<Unit> = runCatchAndLog { Transaction.openOuter().use(action) }
    .onFailure { e -> HTLogger.log { it.throwing(e) } }
