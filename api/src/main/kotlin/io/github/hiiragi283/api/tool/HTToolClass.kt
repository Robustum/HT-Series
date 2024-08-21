package io.github.hiiragi283.api.tool

import io.github.hiiragi283.api.item.tool.HTBowItem
import io.github.hiiragi283.api.item.tool.HTCrossBowItem
import io.github.hiiragi283.api.item.tool.HTMiningToolItem
import io.github.hiiragi283.api.item.tool.HTSwordItem
import io.github.hiiragi283.api.tool.behavior.HTToolBehavior
import net.fabricmc.fabric.api.tag.TagRegistry
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.item.Item
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier

enum class HTToolClass(
    val toolTag: Tag<Item>,
    val defaultBehaviors: List<HTToolBehavior>,
    val toolFunction: (HTToolProperty) -> HTTool,
) {
    AXE(FabricToolTags.AXES, HTToolBehavior.LOG_STRIPPING, HTMiningToolItem::axe),
    HOE(FabricToolTags.HOES, HTToolBehavior.HOT_TILLING, HTMiningToolItem::hoe),
    PICKAXE(FabricToolTags.PICKAXES, HTToolBehavior.TORCH_PLACING, HTMiningToolItem::pickaxe),
    SHOVEL(FabricToolTags.SHOVELS, HTToolBehavior.SHOVEL_FLATTENING, HTMiningToolItem::shovel),

    // SHEARS(FabricToolTags.SHEARS, ::HTSwordItem),
    SWORD(FabricToolTags.SWORDS, ::HTSwordItem),
    BOW(TagRegistry.item(Identifier("fabric", "bows")), ::HTBowItem),
    CROSSBOW(TagRegistry.item(Identifier("fabric", "crossbows")), ::HTCrossBowItem),
    ;

    constructor(toolTag: Tag<Item>, toolFunction: (HTToolProperty) -> HTTool) : this(toolTag, emptyList(), toolFunction)

    constructor(
        toolTag: Tag<Item>,
        behavior: HTToolBehavior,
        toolFunction: (HTToolProperty) -> HTTool,
    ) : this(toolTag, listOf(behavior), toolFunction)

    fun isSameTag(other: Tag<Item>): Boolean = other == toolTag

    companion object {
        @JvmStatic
        fun fromTag(toolTag: Tag<Item>): HTToolClass? = HTToolClass.entries.firstOrNull { it.toolTag == toolTag }
    }
}
