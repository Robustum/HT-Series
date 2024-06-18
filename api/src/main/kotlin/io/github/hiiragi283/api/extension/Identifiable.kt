package io.github.hiiragi283.api.extension

import net.minecraft.util.Identifier

fun interface Identifiable {
    fun asIdentifier(): Identifier
}
