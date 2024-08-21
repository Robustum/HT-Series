package io.github.hiiragi283.api.item.tool

import io.github.hiiragi283.api.tool.HTTool
import io.github.hiiragi283.api.tool.HTToolClass
import io.github.hiiragi283.api.tool.HTToolProperty
import net.minecraft.item.CrossbowItem

class HTCrossBowItem(
    override val toolProperty: HTToolProperty,
) : CrossbowItem(toolProperty.settings), HTTool {
    override val toolClass: HTToolClass = HTToolClass.BOW
}
