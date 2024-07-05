package io.github.hiiragi283.material.compat.rei

import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.material.impl.HTMaterialsAPIImpl
import me.shedaniel.clothconfig2.ClothConfigInitializer
import me.shedaniel.clothconfig2.api.ScissorsHandler
import me.shedaniel.clothconfig2.api.ScrollingContainer
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.REIHelper
import me.shedaniel.rei.api.RecipeCategory
import me.shedaniel.rei.api.widgets.Slot
import me.shedaniel.rei.api.widgets.Widgets
import me.shedaniel.rei.gui.widget.Widget
import me.shedaniel.rei.gui.widget.WidgetWithBounds
import net.minecraft.client.gui.Element
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper

object HTMaterialCategory : RecipeCategory<HTMaterialDisplay> {
    override fun getIdentifier(): Identifier = HMREIPlugin.MATERIAL

    override fun getLogo(): EntryStack = EntryStack.create(HTMaterialsAPIImpl.iconItem)

    override fun getCategoryName(): String = HTModuleType.MATERIAL.modName

    override fun setupDisplay(display: HTMaterialDisplay, bounds: Rectangle): List<Widget> = buildList {
        this += Widgets
            .createSlot(Point(bounds.centerX - 8, bounds.y + 3))
            .entry(display.icon)
        val rectangle = Rectangle(
            bounds.centerX - bounds.width / 2 - 1,
            bounds.y + 23,
            bounds.width + 2,
            bounds.height - 28,
        )
        this += Widgets.createSlotBase(rectangle)
        this += HTScrollableSlotsWidget(
            rectangle,
            display.entries.map { entry: EntryStack ->
                Widgets.createSlot(Point(0, 0))
                    .disableBackground()
                    .entry(entry)
            },
        )
    }

    override fun getDisplayHeight(): Int = 140

    private class HTScrollableSlotsWidget(
        private val bounds: Rectangle,
        private val widgets: List<Slot>,
    ) : WidgetWithBounds() {
        private val scrolling: ScrollingContainer = object : ScrollingContainer() {
            override fun getBounds(): Rectangle = Rectangle(
                this@HTScrollableSlotsWidget.getBounds().x + 1,
                this@HTScrollableSlotsWidget.getBounds().y + 1,
                this@HTScrollableSlotsWidget.getBounds().width - 2,
                this@HTScrollableSlotsWidget.getBounds().height - 2,
            )

            override fun getMaxScrollHeight(): Int = MathHelper.ceil(widgets.size / 8f) * 18
        }

        override fun mouseScrolled(double1: Double, double2: Double, double3: Double): Boolean = when {
            containsMouse(double1, double2) -> {
                scrolling.offset(ClothConfigInitializer.getScrollStep() * -double3, true)
                true
            }

            else -> false
        }

        override fun getBounds(): Rectangle = bounds

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean = when {
            scrolling.updateDraggingState(mouseX, mouseY, button) -> true
            else -> super.mouseClicked(mouseX, mouseY, button)
        }

        override fun mouseDragged(
            mouseX: Double,
            mouseY: Double,
            button: Int,
            deltaX: Double,
            deltaY: Double,
        ): Boolean = when {
            scrolling.mouseDragged(mouseX, mouseY, button, deltaX, deltaY) -> true
            else -> super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
        }

        override fun render(
            matrices: MatrixStack,
            mouseX: Int,
            mouseY: Int,
            delta: Float,
        ) {
            scrolling.updatePosition(delta)
            ScissorsHandler.INSTANCE.scissor(scrolling.scissorBounds)
            (0 until MathHelper.ceil(widgets.size / 8f)).forEach { y: Int ->
                for (x: Int in 0..7) {
                    val index: Int = y * 8 + x
                    if (widgets.size <= index) break
                    widgets[index].apply {
                        this.bounds.setLocation(
                            this@HTScrollableSlotsWidget.bounds.x + 1 + x * 18,
                            this@HTScrollableSlotsWidget.bounds.y + 1 + y * 18 - scrolling.scrollAmount.toInt(),
                        )
                        render(matrices, mouseX, mouseY, delta)
                    }
                }
            }
            ScissorsHandler.INSTANCE.removeLastScissor()
            ScissorsHandler.INSTANCE.scissor(scrolling.bounds)
            scrolling.renderScrollBar(-0x1000000, 1f, if (REIHelper.getInstance().isDarkThemeEnabled) 0.8f else 1f)
            ScissorsHandler.INSTANCE.removeLastScissor()
        }

        override fun children(): List<Element> = widgets
    }
}
