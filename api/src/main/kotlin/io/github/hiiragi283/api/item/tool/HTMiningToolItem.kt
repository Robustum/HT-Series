package io.github.hiiragi283.api.item.tool

import io.github.hiiragi283.api.tool.HTTool
import io.github.hiiragi283.api.tool.HTToolClass
import io.github.hiiragi283.api.tool.HTToolProperty
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.MiningToolItem
import net.minecraft.tag.Tag
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class HTMiningToolItem(
    override val toolProperty: HTToolProperty,
    override val toolClass: HTToolClass,
) : MiningToolItem(0f, 0f, toolProperty, setOf(), toolProperty.settings), HTTool {
    companion object {
        @JvmStatic
        fun axe(toolProperty: HTToolProperty): HTMiningToolItem = HTMiningToolItem(toolProperty, HTToolClass.AXE)

        @JvmStatic
        fun hoe(toolProperty: HTToolProperty): HTMiningToolItem = HTMiningToolItem(toolProperty, HTToolClass.HOE)

        @JvmStatic
        fun pickaxe(toolProperty: HTToolProperty): HTMiningToolItem = HTMiningToolItem(toolProperty, HTToolClass.PICKAXE)

        @JvmStatic
        fun shovel(toolProperty: HTToolProperty): HTMiningToolItem = HTMiningToolItem(toolProperty, HTToolClass.SHOVEL)
    }

    /*override fun getMiningSpeedMultiplier(stack: ItemStack, state: BlockState): Float =
        super<MiningToolItem>.getMiningSpeedMultiplier(stack, state)*/

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean = postHitWithTool(stack, target, attacker)

    override fun postMine(
        stack: ItemStack,
        world: World,
        state: BlockState,
        pos: BlockPos,
        miner: LivingEntity,
    ): Boolean = postMineWithTool(stack, world, state, pos, miner)

    override fun useOnBlock(context: ItemUsageContext): ActionResult = useOnBlockWithTool(context)

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> = useWithTool(world, user, hand)

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext,
    ) {
        behaviorList.forEach { it.appendTooltip(stack, world, tooltip, context) }
    }

    //   DynamicAttributeTool    //

    override fun getMiningLevel(
        tag: Tag<Item>,
        state: BlockState,
        stack: ItemStack,
        user: LivingEntity?,
    ): Int = if (toolClass.isSameTag(tag)) material.miningLevel else 0

    override fun getMiningSpeedMultiplier(
        tag: Tag<Item>,
        state: BlockState,
        stack: ItemStack,
        user: LivingEntity?,
    ): Float = if (toolClass.isSameTag(tag)) material.miningSpeedMultiplier else 0f
}
