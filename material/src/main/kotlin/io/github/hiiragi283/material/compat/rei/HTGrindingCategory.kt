package io.github.hiiragi283.material.compat.rei

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

    override fun setupDisplay(recipeDisplay: HTGrindingDisplay, bounds: Rectangle): List<Widget> = buildList {
        val startPoint = Point(bounds.centerX - 41, bounds.centerY - 13)
        add(Widgets.createRecipeBase(bounds))
        add(Widgets.createArrow(Point(startPoint.x + 27, startPoint.y + 4)))
        add(Widgets.createResultSlotBackground(Point(startPoint.x + 61, startPoint.y + 5)))
        add(
            Widgets.createSlot(Point(startPoint.x + 4, startPoint.y + 5))
                .entries(recipeDisplay.inputEntries.getOrNull(0) ?: listOf()).markInput(),
        )
        add(
            Widgets.createSlot(Point(startPoint.x + 61, startPoint.y + 5))
                .entries(recipeDisplay.resultingEntries.getOrNull(0) ?: listOf())
                .disableBackground()
                .markOutput(),
        )
    }

    override fun getDisplayHeight(): Int = 36
}
