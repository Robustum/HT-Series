package io.github.hiiragi283.engineering.client

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.hiiragi283.api.gui.HTCottonInventoryScreen
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.multiblock.HTMultiblockRenderer
import io.github.hiiragi283.api.resource.HTModelJsonBuilder
import io.github.hiiragi283.api.resource.HTRuntimeClientPack
import io.github.hiiragi283.api.screen.HTSlot2xScreenHandler
import io.github.hiiragi283.engineering.common.init.HEBlockEntityTypes
import io.github.hiiragi283.engineering.common.init.HEBlocks
import io.github.hiiragi283.engineering.common.init.HEItems
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.block.Blocks
import net.minecraft.data.client.model.BlockStateVariant
import net.minecraft.data.client.model.ModelIds
import net.minecraft.data.client.model.VariantSettings
import net.minecraft.data.client.model.VariantsBlockStateSupplier
import net.minecraft.screen.ScreenHandlerType

object HTEngineeringClient : ClientModInitializer {
    override fun onInitializeClient() {
        registerBlockStates()
        registerModels()

        registerScreen(HTSlot2xScreenHandler.TYPE)

        BlockEntityRendererRegistry.INSTANCE.register(HEBlockEntityTypes.PRIMITIVE_BLAST_FURNACE, ::HTMultiblockRenderer)

        HTLogger.log { it.info("HT Engineering Client Initialized!") }
    }

    private fun registerBlockStates() {
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.BRICK_WRAPPED_TERRACOTTA)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.STEEL_PLATED_STONE)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.STEEL_WRAPPED_STONE)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.DREADY_WRAPPED_STONE)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.STEADY_WRAPPED_STONE)
        HTRuntimeClientPack.addBlockState(HEBlocks.CAULDRON) { block ->
            VariantsBlockStateSupplier.create(
                block,
                BlockStateVariant.create()
                    .put(VariantSettings.MODEL, ModelIds.getBlockModelId(Blocks.CAULDRON)),
            )
        }
    }

    private fun registerModels() {
        HTRuntimeClientPack.addBlockItemModel(HEItems.BRICK_WRAPPED_TERRACOTTA, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockItemModel(HEItems.STEEL_PLATED_STONE, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockItemModel(HEItems.STEEL_WRAPPED_STONE, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockItemModel(HEItems.DREADY_WRAPPED_STONE, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockItemModel(HEItems.STEADY_WRAPPED_STONE, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockItemModel(HEItems.CAULDRON) { builder, _ ->
            HTModelJsonBuilder.blockParented(builder, Blocks.CAULDRON)
        }
    }

    private fun <T : SyncedGuiDescription> registerScreen(handler: ScreenHandlerType<T>) {
        ScreenRegistry.register(handler, ::HTCottonInventoryScreen)
    }
}
