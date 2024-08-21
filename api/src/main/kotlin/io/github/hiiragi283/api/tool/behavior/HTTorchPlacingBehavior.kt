package io.github.hiiragi283.api.tool.behavior

import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.WallTorchBlock
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

object HTTorchPlacingBehavior : HTToolBehavior {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world: World = context.world
        val pos: BlockPos = context.blockPos
        val side: Direction = context.side
        val pos1: BlockPos = pos.offset(side)
        if (side != Direction.DOWN && world.getBlockState(pos1).isAir) {
            val player: PlayerEntity = context.player ?: return ActionResult.PASS
            for (stack: ItemStack in player.inventory.main) {
                if (stack.item == Items.TORCH) {
                    val state: BlockState = if (side == Direction.UP) {
                        Blocks.TORCH.defaultState
                    } else {
                        Blocks.WALL_TORCH.defaultState.with(WallTorchBlock.FACING, side.opposite)
                    }
                    if (!world.isClient) {
                        world.setBlockState(pos1, state)
                    }
                    return ActionResult.success(world.isClient)
                }
            }
        }
        return ActionResult.PASS
    }
}
