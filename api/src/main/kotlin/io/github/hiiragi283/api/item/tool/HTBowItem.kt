package io.github.hiiragi283.api.item.tool

import io.github.hiiragi283.api.tool.HTTool
import io.github.hiiragi283.api.tool.HTToolClass
import io.github.hiiragi283.api.tool.HTToolProperty
import net.minecraft.item.BowItem

class HTBowItem(override val toolProperty: HTToolProperty) : BowItem(toolProperty.settings), HTTool {
    override val toolClass: HTToolClass = HTToolClass.BOW
}
