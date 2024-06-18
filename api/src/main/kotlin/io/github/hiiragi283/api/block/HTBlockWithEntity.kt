package io.github.hiiragi283.api.block

import io.github.hiiragi283.api.block.entity.HTBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.Inventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.ItemScatterer
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
abstract class HTBlockWithEntity(settings: Settings) : Block(settings), BlockEntityProvider {
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult,
    ): ActionResult = (world.getBlockEntity(pos) as? HTBlockEntity)?.onUse(state, world, pos, player, hand, hit) ?: super.onUse(
        state,
        world,
        pos,
        player,
        hand,
        hit,
    )

    override fun onStateReplaced(
        state: BlockState,
        world: World,
        pos: BlockPos,
        newState: BlockState,
        moved: Boolean,
    ) {
        if (state.isOf(newState.block)) return
        world.getBlockEntity(pos)
            ?.let { it as? Inventory }
            ?.let {
                ItemScatterer.spawn(world, pos, it)
                world.updateComparators(pos, this)
            }
        super.onStateReplaced(state, world, pos, newState, moved)
    }

    override fun onSyncedBlockEvent(
        state: BlockState,
        world: World,
        pos: BlockPos,
        type: Int,
        data: Int,
    ): Boolean {
        super.onSyncedBlockEvent(state, world, pos, type, data)
        return world.getBlockEntity(pos)?.onSyncedBlockEvent(type, data) ?: false
    }

    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory? =
        world.getBlockEntity(pos) as? NamedScreenHandlerFactory
}
