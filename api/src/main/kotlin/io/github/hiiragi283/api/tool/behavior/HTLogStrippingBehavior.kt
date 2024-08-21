package io.github.hiiragi283.api.tool.behavior

import io.github.hiiragi283.api.tool.HTBlockReformingRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.PillarBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemUsageContext
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object HTLogStrippingBehavior : HTToolBehavior {
    //    HTToolBehavior    //

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world: World = context.world
        val pos: BlockPos = context.blockPos
        val state: BlockState = world.getBlockState(pos)
        val stripped: BlockState = HTBlockReformingRegistry.getStripped(state.block) ?: return ActionResult.PASS
        val player: PlayerEntity? = context.player
        world.playSound(player, pos, SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0f, .0f)
        if (!world.isClient) {
            world.setBlockState(
                pos,
                stripped.with(
                    PillarBlock.AXIS,
                    state.get(PillarBlock.AXIS),
                ),
                11,
            )
            player?.let {
                context.stack.damage(1, it) { player1 ->
                    player1.sendToolBreakStatus(context.hand)
                }
            }
        }
        return ActionResult.success(world.isClient)
    }
}
