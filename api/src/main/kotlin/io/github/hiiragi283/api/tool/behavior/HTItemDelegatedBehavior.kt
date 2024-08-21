package io.github.hiiragi283.api.tool.behavior

import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class HTItemDelegatedBehavior(private val item: Item) : HTToolBehavior {
    constructor(itemConvertible: ItemConvertible) : this(itemConvertible.asItem())

    override fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {
        item.postHit(stack, target, attacker)
    }

    override fun postMine(
        stack: ItemStack,
        world: World,
        state: BlockState,
        pos: BlockPos,
        miner: LivingEntity,
    ) {
        item.postMine(stack, world, state, pos, miner)
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult = item.useOnBlock(context)

    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> = item.use(world, user, hand)

    override fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext,
    ) {
        item.appendTooltip(stack, world, tooltip, context)
    }
}
