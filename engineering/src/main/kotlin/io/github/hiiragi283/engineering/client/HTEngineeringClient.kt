package io.github.hiiragi283.engineering.client

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.hiiragi283.api.extension.singleBlockStateFunction
import io.github.hiiragi283.api.gui.HTCottonInventoryScreen
import io.github.hiiragi283.api.machine.multiblock.HTMultiblockRenderer
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.resource.HTModelJsonBuilder
import io.github.hiiragi283.api.resource.HTRuntimeClientPack
import io.github.hiiragi283.api.screen.HTMachineScreenHandler
import io.github.hiiragi283.engineering.common.init.HEBlocks
import io.github.hiiragi283.engineering.common.init.HEItems
import io.github.hiiragi283.engineering.common.init.HEMachineTypes
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.minecraft.block.Blocks
import net.minecraft.data.client.model.Models
import net.minecraft.data.client.model.TextureKey
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

object HTEngineeringClient : ClientModInitializer {
    override fun onInitializeClient() {
        registerBlockStates()
        registerModels()

        registerScreen(HTMachineScreenHandler.TYPE)

        BlockEntityRendererRegistry.INSTANCE.register(
            HEMachineTypes.PRIMITIVE_BLAST_FURNACE,
            ::HTMultiblockRenderer,
        )

        HTLogger.log { it.info("HT Engineering Client Initialized!") }
    }

    private fun registerBlockStates() {
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.BRICK_WRAPPED_TERRACOTTA)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.STEEL_PLATED_STONE)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.STEEL_WRAPPED_STONE)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.DREADY_WRAPPED_STONE)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.STEADY_WRAPPED_STONE)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.STEEL_HULL)
        HTRuntimeClientPack.addSimpleBlockState(HEBlocks.BASALT_COBBLESTONE)

        HTRuntimeClientPack.addBlockState(
            HEBlocks.CAULDRON,
            singleBlockStateFunction(Blocks.CAULDRON),
        )
    }

    private fun registerModels() {
        HTRuntimeClientPack.addBlockItemModel(HEItems.BRICK_WRAPPED_TERRACOTTA, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockItemModel(HEItems.STEEL_PLATED_STONE, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockItemModel(HEItems.STEEL_WRAPPED_STONE, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockItemModel(HEItems.DREADY_WRAPPED_STONE, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockItemModel(HEItems.STEADY_WRAPPED_STONE, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockModel(HEBlocks.STEEL_HULL) { builder, _ ->
            builder.setParent(Models.CUBE_ALL)
            builder.addTexture(TextureKey.ALL, HTModuleType.API.id("block/machine_hull"))
        }
        HTRuntimeClientPack.addBlockItemModel(HEItems.STEEL_HULL, HTModelJsonBuilder::blockParented)
        HTRuntimeClientPack.addBlockModel(HEBlocks.BASALT_COBBLESTONE) { builder, _ ->
            builder.setParent(Models.CUBE_ALL)
            builder.addTexture(TextureKey.ALL, Identifier("block/basalt_top"))
        }
        HTRuntimeClientPack.addBlockItemModel(HEItems.BASALT_COBBLESTONE, HTModelJsonBuilder::blockParented)

        HTRuntimeClientPack.addBlockItemModel(HEItems.CAULDRON) { builder, _ ->
            HTModelJsonBuilder.blockParented(builder, Blocks.CAULDRON)
        }
    }

    private fun <T : SyncedGuiDescription> registerScreen(handler: ScreenHandlerType<T>) {
        ScreenRegistry.register(handler, ::HTCottonInventoryScreen)
    }
}
