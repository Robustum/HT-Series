package io.github.hiiragi283.api.storage

import net.minecraft.util.math.Direction

class HTSidedStorageBuilder(size: Int) {
    internal val ioTypes: Array<HTStorageIO> = Array(size) { HTStorageIO.GENERIC }
    internal val sides: Map<Direction, List<Int>>
        get() = sides1
    private val sides1: MutableMap<Direction, MutableList<Int>> = mutableMapOf()

    operator fun set(index: Int, type: HTStorageIO, sideType: HTStorageSide): HTSidedStorageBuilder = apply {
        ioTypes[index] = type
        sideType.directions.forEach { direction: Direction ->
            sides1.computeIfAbsent(direction) { mutableListOf() }.add(index)
        }
    }

    fun getSideSlots(side: Direction): List<Int> = sides.getOrDefault(side, emptyList())

    fun getSideSlotArray(side: Direction): IntArray = getSideSlots(side).toIntArray()

    fun <T> build(function: (HTSidedStorageBuilder) -> T) = function(this)
}
