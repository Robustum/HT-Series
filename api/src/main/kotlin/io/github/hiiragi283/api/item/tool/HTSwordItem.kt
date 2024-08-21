package io.github.hiiragi283.api.item.tool

import io.github.hiiragi283.api.tool.HTTool
import io.github.hiiragi283.api.tool.HTToolClass
import io.github.hiiragi283.api.tool.HTToolProperty
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.SwordItem
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class HTSwordItem(
    override val toolProperty: HTToolProperty,
) : SwordItem(toolProperty, 0, 0f, toolProperty.settings), HTTool {
    override val toolClass: HTToolClass = HTToolClass.SWORD

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
}
