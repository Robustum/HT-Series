package io.github.hiiragi283.api.extension

import me.shedaniel.math.Point

fun Point.modify(x: Int, y: Int) = Point(this.x + x, this.y + y)
