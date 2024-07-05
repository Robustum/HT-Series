package io.github.hiiragi283.api.block.entity

import io.github.hiiragi283.api.machine.HTMachineType
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class HTMultiblockControllerBlockEntity(
    override val machineType: HTMachineType.Multi<*>,
) : HTMachineBlockEntity(machineType) {
    var showPreview: Boolean = false
        protected set

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult,
    ): ActionResult {
        if (hand == Hand.MAIN_HAND) {
            if (player.isSneaking) {
                showPreview = !showPreview
                return ActionResult.success(world.isClient)
            }
            if (machineType.multiblockShape.test(world, pos, player)) {
                return super.onUse(state, world, pos, player, hand, hit)
            }
            showPreview = false
        }
        return ActionResult.PASS
    }
}
