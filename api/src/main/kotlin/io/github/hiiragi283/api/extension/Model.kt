package io.github.hiiragi283.api.extension

import net.minecraft.block.Block
import net.minecraft.data.client.model.*
import net.minecraft.state.property.Properties
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

fun createSingleBlockState(block: Block, modelId: Identifier = ModelIds.getBlockModelId(block)): VariantsBlockStateSupplier =
    VariantsBlockStateSupplier.create(
        block,
        BlockStateVariant.create().put(VariantSettings.MODEL, modelId),
    )

fun VariantsBlockStateSupplier.horizontalCoordinate(): VariantsBlockStateSupplier = coordinate(
    BlockStateVariantMap.create(Properties.HORIZONTAL_FACING)
        .register(
            Direction.EAST,
            BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90),
        )
        .register(
            Direction.SOUTH,
            BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180),
        )
        .register(
            Direction.WEST,
            BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270),
        )
        .register(Direction.NORTH, BlockStateVariant.create()),
)

val singleBlockStateFunction: (Block) -> VariantsBlockStateSupplier =
    { createSingleBlockState(it, ModelIds.getBlockModelId(it)) }

fun singleBlockStateFunction(modelId: Identifier): (Block) -> VariantsBlockStateSupplier = { createSingleBlockState(it, modelId) }

fun singleBlockStateFunction(parent: Block): (Block) -> VariantsBlockStateSupplier =
    { createSingleBlockState(it, ModelIds.getBlockModelId(parent)) }
