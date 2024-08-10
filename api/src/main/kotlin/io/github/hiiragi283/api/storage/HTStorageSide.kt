package io.github.hiiragi283.api.storage

import net.minecraft.util.math.Direction

enum class HTStorageSide(val directions: Collection<Direction>) {
    UP(setOf(Direction.UP)),
    SIDE(setOf(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)),
    DOWN(setOf(Direction.DOWN)),
    ANY(Direction.entries),
    NONE(emptySet()),
}
