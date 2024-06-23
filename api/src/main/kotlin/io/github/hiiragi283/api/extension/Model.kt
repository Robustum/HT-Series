package io.github.hiiragi283.api.extension

import net.minecraft.block.Block
import net.minecraft.data.client.model.BlockStateSupplier
import net.minecraft.data.client.model.BlockStateVariant
import net.minecraft.data.client.model.ModelIds
import net.minecraft.data.client.model.VariantSettings
import net.minecraft.data.client.model.VariantsBlockStateSupplier
import net.minecraft.util.Identifier

fun createSingleBlockState(block: Block, modelId: Identifier = ModelIds.getBlockModelId(block)): BlockStateSupplier =
    VariantsBlockStateSupplier.create(
        block,
        BlockStateVariant.create().put(VariantSettings.MODEL, modelId),
    )

val singleBlockStateFunction: (Block) -> BlockStateSupplier =
    { createSingleBlockState(it, ModelIds.getBlockModelId(it)) }

fun singleBlockStateFunction(modelId: Identifier): (Block) -> BlockStateSupplier = { createSingleBlockState(it, modelId) }

fun singleBlockStateFunction(parent: Block): (Block) -> BlockStateSupplier =
    { createSingleBlockState(it, ModelIds.getBlockModelId(parent)) }
