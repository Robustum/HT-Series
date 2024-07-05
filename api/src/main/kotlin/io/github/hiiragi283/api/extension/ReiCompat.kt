package io.github.hiiragi283.api.extension

import io.github.hiiragi283.api.recipe.HTIngredient
import io.github.hiiragi283.api.recipe.HTResult
import me.shedaniel.math.Point
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.gui.widget.Widget

fun Point.modify(x: Int, y: Int): Point = Point(this.x + x, this.y + y)

inline fun buildDisplay(point: Point, builderAction: (MutableList<Widget>, Point) -> Unit): List<Widget> =
    buildList { builderAction(this, point) }

fun HTIngredient.toEntryStack(): List<EntryStack> = EntryStack.ofItemStacks(previewStacks)

fun HTResult.toEntryStack(): List<EntryStack> = EntryStack.ofItemStacks(previewStacks)
