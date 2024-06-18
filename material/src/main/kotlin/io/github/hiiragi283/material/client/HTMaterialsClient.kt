package io.github.hiiragi283.material.client

import io.github.hiiragi283.api.extension.useOuterTransaction
import io.github.hiiragi283.api.material.HTMaterial
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.module.HTPluginHolder
import io.github.hiiragi283.api.resource.HTModelJsonBuilder
import io.github.hiiragi283.api.resource.HTRuntimeClientPack
import io.github.hiiragi283.material.client.gui.screen.MaterialDictionaryScreen
import io.github.hiiragi283.material.common.HTMaterials
import io.github.hiiragi283.material.common.block.HTMaterialLibraryBlock
import io.github.hiiragi283.material.impl.HTMaterialsAPIImpl
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.model.*
import net.minecraft.fluid.Fluid
import net.minecraft.item.ItemStack
import net.minecraft.state.property.Properties
import net.minecraft.tag.ItemTags
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction

@Environment(EnvType.CLIENT)
object HTMaterialsClient : ClientModInitializer {
    override fun onInitializeClient() {
        ScreenRegistry.register(HTMaterials.screenHandlerType, ::MaterialDictionaryScreen)

        HTRuntimeClientPack.addBlockState(HTMaterialLibraryBlock) { block ->
            VariantsBlockStateSupplier.create(
                block,
                BlockStateVariant.create()
                    .put(VariantSettings.MODEL, ModelIds.getBlockModelId(block)),
            ).coordinate(
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
        }
        HTRuntimeClientPack.addItemModel(HTMaterialLibraryBlock.asItem()) { builder, _ ->
            builder.parentId = ModelIds.getBlockModelId(HTMaterialLibraryBlock)
        }
        HTRuntimeClientPack.addItemModel(HTMaterialsAPIImpl.iconItem) { builder, _ ->
            HTModelJsonBuilder.simpleItemModel(builder, HTModuleType.API.id("icon/material"))
        }
        HTRuntimeClientPack.addItemModel(HTMaterialsAPIImpl.dictionaryItem) { builder, _ ->
            HTModelJsonBuilder.simpleItemModel(builder, HTModuleType.API.id("icon/api"))
        }

        ItemTooltipCallback.EVENT.register(HTMaterialsClient::getTooltip)

        HTPluginHolder.Material.forEach {
            it.afterMaterialRegistration(HTApiHolder.Material.apiInstance, true)
        }

        HTLogger.log { it.info("HT Materials Client initialized!") }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getTooltip(stack: ItemStack, context: TooltipContext, lines: MutableList<Text>) {
        if (stack.isEmpty) return
        buildList {
            // Part tooltip on item
            HTApiHolder.Material.apiInstance.materialItemManager[stack]
                ?.let { HTMaterial.TooltipContext(it.material, it.shapeKey, stack, lines) }
                ?.let(::add)
            // Part tooltip on fluid container item
            collectFluids(stack)
                .mapNotNull(HTApiHolder.Material.apiInstance.materialFluidManager::get)
                .map { HTMaterial.TooltipContext(it.material, it.phase, ItemStack.EMPTY, lines) }
                .forEach(::add)
        }.forEach(HTMaterial.Companion::appendTooltip)
        // Tag tooltips (only dev)
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            ItemTags.getTagGroup().getTagsFor(stack.item).forEach { id: Identifier ->
                lines.add(LiteralText(id.toString()))
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun collectFluids(stack: ItemStack): Set<Fluid> = buildSet {
        // From Fabric API
        useOuterTransaction { transaction ->
            FluidStorage.ITEM.find(stack, ContainerItemContext.withInitial(stack))
                ?.iterable(transaction)
                ?.map(StorageView<FluidVariant>::getResource)
                ?.map(FluidVariant::getFluid)
                ?.forEach(::add)
        }
        // From LibBlockAttributes if loaded
        /*if (FabricLoader.getInstance().isModLoaded("libblockattributes")) {
            FluidAttributes.GROUPED_INV_VIEW.get(stack).storedFluids
                .mapNotNull(FluidKey::getRawFluid)
                .forEach(::add)
        }*/
    }
}
