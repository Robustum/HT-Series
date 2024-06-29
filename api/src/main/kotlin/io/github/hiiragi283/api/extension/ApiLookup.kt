package io.github.hiiragi283.api.extension

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

fun <A : Any, C : Any> BlockApiLookup<A, C>.tryFind(
    world: World,
    pos: BlockPos,
    context: C,
    state: BlockState? = world.getBlockState(pos),
    blockEntity: BlockEntity? = world.getBlockEntity(pos),
): Result<A?> = runCatchAndLog { find(world, pos, state, blockEntity, context) }

fun <A : Any, C : Any> BlockApiLookup<A, C>.findFrom(world: World, pos: BlockPos, context: C): A? =
    find(world, pos, world.getBlockState(pos), world.getBlockEntity(pos), context)
