package io.github.hiiragi283.material.compat.rei

import io.github.hiiragi283.api.extension.buildDisplay
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeCategory
import me.shedaniel.rei.api.widgets.Widgets
import me.shedaniel.rei.gui.widget.Widget
import net.minecraft.block.Blocks
import net.minecraft.client.resource.language.I18n
import net.minecraft.util.Identifier
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
object HTGrindingCategory : RecipeCategory<HTGrindingDisplay> {
    override fun getIdentifier(): Identifier = HMREIPlugin.GRINDING

    override fun getLogo(): EntryStack = EntryStack.create(Blocks.GRINDSTONE)

    override fun getCategoryName(): String = I18n.translate("category.ht_materials.grinding")

    override fun setupDisplay(recipeDisplay: HTGrindingDisplay, bounds: Rectangle): List<Widget> =
        buildDisplay(Point(bounds.centerX - 41, bounds.centerY - 13)) { list, point ->
            list.add(Widgets.createRecipeBase(bounds))
            list.add(Widgets.createArrow(Point(point.x + 27, point.y + 4)))
            list.add(Widgets.createResultSlotBackground(Point(point.x + 61, point.y + 5)))
            list.add(
                Widgets.createSlot(Point(point.x + 4, point.y + 5))
                    .entries(recipeDisplay.inputEntries.getOrNull(0) ?: listOf()).markInput(),
            )
            list.add(
                Widgets.createSlot(Point(point.x + 61, point.y + 5))
                    .entries(recipeDisplay.resultingEntries.getOrNull(0) ?: listOf())
                    .disableBackground()
                    .markOutput(),
            )
        }

    override fun getDisplayHeight(): Int = 36
}
