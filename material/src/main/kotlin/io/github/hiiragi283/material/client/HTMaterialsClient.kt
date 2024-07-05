package io.github.hiiragi283.material.client

import io.github.hiiragi283.api.extension.*
import io.github.hiiragi283.api.fluid.HTMaterialFluidVariantRenderHandler
import io.github.hiiragi283.api.fluid.HTSimpleFluidRenderHandler
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialTooltipContext
import io.github.hiiragi283.api.material.content.HTMaterialContentManager
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.material.type.HTMaterialType
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.module.HTPluginHolder
import io.github.hiiragi283.api.property.HTPropertyHolder
import io.github.hiiragi283.api.resource.HTModelJsonBuilder
import io.github.hiiragi283.api.resource.HTRuntimeClientPack
import io.github.hiiragi283.material.client.gui.screen.MaterialDictionaryScreen
import io.github.hiiragi283.material.common.HTMaterials
import io.github.hiiragi283.material.common.block.HTMaterialLibraryBlock
import io.github.hiiragi283.material.impl.HTMaterialsAPIImpl
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.minecraft.block.Block
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.item.TooltipContext
import net.minecraft.data.client.model.*
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

@Environment(EnvType.CLIENT)
object HTMaterialsClient : ClientModInitializer {
    override fun onInitializeClient() {
        ScreenRegistry.register(HTMaterials.screenHandlerType, ::MaterialDictionaryScreen)

        registerResource()
        registerMaterialResources()

        ItemTooltipCallback.EVENT.register(HTMaterialsClient::getTooltip)

        HTPluginHolder.Material.forEach {
            it.afterMaterialRegistration(HTApiHolder.Material.apiInstance, true)
        }

        HTLogger.log { it.info("HT Materials Client initialized!") }
    }

    private fun registerResource() {
        HTRuntimeClientPack.addBlockState(HTMaterialLibraryBlock) { block ->
            createSingleBlockState(block).horizontalCoordinate()
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
    }

    private fun buildItemModel(builder: HTModelJsonBuilder, key: HTMaterialKey, shapeKey: HTShapeKey) {
        val material: HTPropertyHolder = key.get()
        val materialType: HTMaterialType = material.getOrDefault(HTMaterialProperties.TYPE, HTMaterialType.Solid)
        HTModelJsonBuilder.simpleItemModel(
            builder,
            materialType.layer0(shapeKey),
            materialType.layer1(shapeKey),
        )
    }

    private fun registerItemColor(material: HTPropertyHolder, shapeKey: HTShapeKey, itemConvertible: ItemConvertible) {
        material[HTMaterialProperties.COLOR]?.rgb?.let { color ->
            ColorProviderRegistry.ITEM.register(
                material.getOrDefault(
                    HTMaterialProperties.itemColor(shapeKey),
                    ItemColorProvider { _, tintIndex -> if (tintIndex == 0) color else -1 },
                ),
                itemConvertible,
            )
        }
    }

    @Suppress("UnstableApiUsage")
    private fun registerMaterialResources() {
        val contentManager: HTMaterialContentManager = HTApiHolder.Material.apiInstance.materialContentManager
        // Block
        contentManager.blockGroup.forEach { key: HTMaterialKey, shapeKey: HTShapeKey, block: Block ->
            val material: HTPropertyHolder = key.get()
            // Color
            material[HTMaterialProperties.COLOR]?.rgb?.let { color ->
                ColorProviderRegistry.BLOCK.register(
                    material.getOrDefault(
                        HTMaterialProperties.blockColor(shapeKey),
                        BlockColorProvider { _, _, _, tintIndex -> if (tintIndex == 0) color else -1 },
                    ),
                    block,
                )
            }
            // BlockState
            material.getOrDefault(HTMaterialProperties.blockState(shapeKey), singleBlockStateFunction)
                .let { HTRuntimeClientPack.addBlockState(block, it) }
            // Block Model
            material[HTMaterialProperties.blockModel(shapeKey)]
                ?.let { HTRuntimeClientPack.addBlockModel(block, it) }
            // ItemBlock Model
            material[HTMaterialProperties.itemBlockModel(shapeKey)]
                ?.let { HTRuntimeClientPack.addItemModel(block.asItem(), it) }
            // ItemBlock Color
            registerItemColor(material, shapeKey, block)
            // RenderLayer
            material[HTMaterialProperties.blockLayer(shapeKey)]
                ?.let { BlockRenderLayerMap.INSTANCE.putBlock(block, it) }
        }
        // Fluid
        contentManager.fluidGroup.forEach { key: HTMaterialKey, phase: HTFluidPhase, fluid: Fluid ->
            // Render
            val color: Int = key.get().getOrDefault(HTMaterialProperties.COLOR, HTColor.WHITE).rgb
            FluidRenderHandlerRegistry.INSTANCE.register(
                fluid,
                HTSimpleFluidRenderHandler(
                    phase.textureId,
                    phase.textureId,
                    color,
                ),
            )
            FluidVariantRendering.register(
                fluid,
                HTMaterialFluidVariantRenderHandler(key, phase),
            )
        }
        // Item
        contentManager.itemGroup.forEach { key: HTMaterialKey, shapeKey: HTShapeKey, item: Item ->
            val material: HTPropertyHolder = key.get()
            // Color
            registerItemColor(material, shapeKey, item)
            // Model
            HTRuntimeClientPack.addItemModel(item) { builder, _ -> buildItemModel(builder, key, shapeKey) }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getTooltip(stack: ItemStack, context: TooltipContext, lines: MutableList<Text>) {
        if (stack.isEmpty) return
        buildList {
            // Part tooltip on item
            HTApiHolder.Material.apiInstance.materialItemManager[stack]
                ?.let { HTMaterialTooltipContext(it.materialKey, it.material, it.shapeKey, stack) }
                ?.let(::add)
            // Part tooltip on fluid container item
            collectFluids(stack)
                .mapNotNull(HTApiHolder.Material.apiInstance.materialFluidManager::get)
                .map { HTMaterialTooltipContext(it.materialKey, it.material, it.phase) }
                .forEach(::add)
        }.forEach { it.appendTooltips(lines) }
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
