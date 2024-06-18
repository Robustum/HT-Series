package io.github.hiiragi283.api.extension

import net.minecraft.screen.ArrayPropertyDelegate

fun buildPropertyDelegate(array: Array<Int>): ArrayPropertyDelegate = ArrayPropertyDelegate(array.size).apply {
    (0 until size()).forEach { set(it, array[it]) }
}
