package io.github.hiiragi283.api.block

import io.github.hiiragi283.api.block.entity.HTBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock.FACING
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation
import net.minecraft.util.math.Direction

@Suppress("OVERRIDE_DEPRECATION")
class HTMultiblockControllerBlock(
    settings: Settings,
    blockEntityConstructor: () -> HTBlockEntity?,
) : HTSimpleBlockWithEntity(settings, blockEntityConstructor) {
    init {
        defaultState = stateManager.defaultState.with(FACING, Direction.NORTH)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? = defaultState.with(FACING, ctx.playerFacing.opposite)

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun rotate(state: BlockState, rotation: BlockRotation): BlockState = state.with(FACING, rotation.rotate(state.get(FACING)))

    override fun mirror(state: BlockState, mirror: BlockMirror): BlockState = state.rotate(mirror.getRotation(state.get(FACING)))
}
