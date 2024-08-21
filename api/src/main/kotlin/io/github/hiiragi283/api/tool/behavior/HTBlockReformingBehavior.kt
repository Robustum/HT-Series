package io.github.hiiragi283.api.tool.behavior

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemUsageContext
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class HTBlockReformingBehavior(
    private val soundEvent: SoundEvent,
    private val blockReforming: (Block) -> BlockState?,
) : HTToolBehavior {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world: World = context.world
        val pos: BlockPos = context.blockPos
        if (context.side != Direction.DOWN && world.getBlockState(pos.up()).isAir) {
            blockReforming(world.getBlockState(pos).block)?.let { state ->
                val player: PlayerEntity? = context.player
                world.playSound(player, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f)
                if (!world.isClient) {
                    world.setBlockState(pos, state, 11)
                    player?.let {
                        context.stack.damage(1, it) { player1 ->
                            player1.sendToolBreakStatus(context.hand)
                        }
                    }
                }
                return ActionResult.success(world.isClient)
            }
        }
        return ActionResult.PASS
    }
}
