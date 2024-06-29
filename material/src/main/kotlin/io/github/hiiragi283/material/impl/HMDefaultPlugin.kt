package io.github.hiiragi283.material.impl

import io.github.hiiragi283.api.extension.*
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.fluid.phase.HTMaterialFluidManager
import io.github.hiiragi283.api.item.shape.HTMaterialItemManager
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialKeys
import io.github.hiiragi283.api.material.content.HTMaterialContentGroup
import io.github.hiiragi283.api.material.content.HTMaterialOre
import io.github.hiiragi283.api.material.content.HTMaterialStorage
import io.github.hiiragi283.api.material.property.HTConfiguredFeatureProperty
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTMaterialsAPI
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.module.HTPlugin
import io.github.hiiragi283.api.property.HTPropertyHolder
import io.github.hiiragi283.api.recipe.HTGrindingRecipe
import io.github.hiiragi283.api.resource.HTRuntimeDataRegistry
import io.github.hiiragi283.api.resource.recipe.HTCookingRecipeBuilder
import io.github.hiiragi283.api.resource.recipe.HTShapedRecipeBuilder
import io.github.hiiragi283.api.resource.recipe.HTShapelessRecipeBuilder
import net.fabricmc.fabric.api.biome.v1.BiomeModifications
import net.fabricmc.fabric.api.registry.FuelRegistry
import net.minecraft.block.Block
import net.minecraft.fluid.Fluids
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.recipe.Ingredient
import net.minecraft.tag.ItemTags
import net.minecraft.util.registry.BuiltinRegistries
import net.minecraft.util.registry.Registry

internal object HMDefaultPlugin : HTPlugin.Material {
    override val modId: String = HTModuleType.MATERIAL.modId
    override val priority: Int = -100

    private fun registerFeature(property: HTConfiguredFeatureProperty) {
        Registry.register(
            BuiltinRegistries.CONFIGURED_FEATURE,
            property.registryKey.value,
            property.configuredFeature,
        )
        BiomeModifications.addFeature(property::checkBiome, property.step, property.registryKey)
    }

    override fun afterMaterialRegistration(instance: HTMaterialsAPI, isClient: Boolean) {
        instance.materialRegistry.forEach { key: HTMaterialKey, material: HTPropertyHolder ->
            // Feature Registration
            material.forEachProperties { _, property ->
                (property as? HTConfiguredFeatureProperty)?.let(::registerFeature)
            }
            HTMaterialOre.Rock.entries.forEach { rock ->
                HTMaterialOre.Manager.getProperty(key, rock)?.let(::registerFeature)
            }
        }
        // Block
        instance.materialContentManager.blockGroup.forEach { materialKey: HTMaterialKey, shapeKey: HTShapeKey, block: Block ->
            // LootTable
            materialKey.get().getOrDefault(HTMaterialProperties.blockLoot(shapeKey)) {
                buildLootTable {
                    pool(
                        buildLootPool {
                            rolls(1)
                            with(ItemEntry.builder(it))
                            surviveExplosion()
                        },
                    )
                }
            }.let { HTRuntimeDataRegistry.addBlockLootTable(block, it) }
        }
        // Item
        val itemGroup: HTMaterialContentGroup<HTShapeKey, Item> = instance.materialContentManager.itemGroup
        instance.materialRegistry.forEach { key: HTMaterialKey, material: HTPropertyHolder ->
            // Recipes
            registerIngotDecomposeRecipe(key, itemGroup)
            registerIngotConstructRecipe(key, itemGroup)
            // If default shape exists
            material[HTMaterialProperties.DEFAULT_ITEM_SHAPE]
                ?.let { defaultKey ->
                    registerGearConstructRecipe(key, defaultKey, itemGroup)
                    if (material[HTMaterialProperties.DEFAULT_ITEM_SHAPE] == HTShapeKeys.INGOT) {
                        registerChunkSmeltingRecipe(key, defaultKey, itemGroup)
                    }
                    if (material[HTMaterialProperties.DEFAULT_ITEM_SHAPE] == HTShapeKeys.GEM) {
                        registerChunkGrindingRecipe(key, defaultKey, itemGroup)
                    }
                }
            // If HTStorageBlockRecipe property exists
            material[HTMaterialProperties.STORAGE]?.let { property: HTMaterialStorage ->
                property.constructRecipe?.let(HTRuntimeDataRegistry::addRecipe)
                property.decomposeRecipe?.let(HTRuntimeDataRegistry::addRecipe)
            }
        }
        // Item Misc
        FuelRegistry.INSTANCE.add(
            HTShapeKeys.BLOCK.get().getItemTag(HTMaterialKeys.COKE),
            200 * 16 * 10,
        )
        FuelRegistry.INSTANCE.add(
            HTShapeKeys.BLOCK.get().getItemTag(HTMaterialKeys.COKE),
            200 * 16,
        )
        // Client-init
        // if (isClient) afterMaterialRegisteredOnClient(instance)
    }

    // Ingot -> 9x Nuggets
    private fun registerIngotDecomposeRecipe(key: HTMaterialKey, itemGroup: HTMaterialContentGroup<HTShapeKey, Item>) {
        itemGroup.getOrNull(key, HTShapeKeys.NUGGET)?.run {
            HTRuntimeDataRegistry.addRecipe(
                HTShapelessRecipeBuilder()
                    .output { ItemStack(this, 9) }
                    .inputs(1) {
                        set(0, Ingredient.fromTag(HTShapeKeys.INGOT.get().getItemTag(key)))
                    }
                    .build(HTShapeKeys.BLOCK.get().getId(key)),
            )
        }
    }

    // 9x Nuggets -> Ingot
    private fun registerIngotConstructRecipe(key: HTMaterialKey, itemGroup: HTMaterialContentGroup<HTShapeKey, Item>) {
        itemGroup.getOrNull(key, HTShapeKeys.INGOT)?.run {
            HTRuntimeDataRegistry.addRecipe(
                HTShapedRecipeBuilder()
                    .inputs(3, 3) {
                        (0..8).forEach {
                            set(
                                it,
                                Ingredient.fromTag(HTShapeKeys.NUGGET.get().getItemTag(key)),
                            )
                        }
                    }
                    .output { this.defaultStack }
                    .build(HTShapeKeys.INGOT.get().getId(key)),
            )
        }
    }

    // 4x Ingots/Gem + Iron Nugget -> Gear
    private fun registerGearConstructRecipe(
        materialKey: HTMaterialKey,
        shapeKey: HTShapeKey,
        itemGroup: HTMaterialContentGroup<HTShapeKey, Item>,
    ) {
        itemGroup.getOrNull(materialKey, HTShapeKeys.GEAR)?.run {
            HTRuntimeDataRegistry.addRecipe(
                HTShapedRecipeBuilder()
                    .inputs(3, 3) {
                        set(1, Ingredient.fromTag(shapeKey.get().getItemTag(materialKey)))
                        set(3, Ingredient.fromTag(shapeKey.get().getItemTag(materialKey)))
                        set(4, Ingredient.ofItems(Items.IRON_NUGGET))
                        set(5, Ingredient.fromTag(shapeKey.get().getItemTag(materialKey)))
                        set(7, Ingredient.fromTag(shapeKey.get().getItemTag(materialKey)))
                    }
                    .output { this.defaultStack }
                    .build(HTShapeKeys.GEAR.get().getId(materialKey)),
            )
        }
    }

    // 1x Chunk -> 1x Ingot
    private fun registerChunkSmeltingRecipe(
        materialKey: HTMaterialKey,
        shapeKey: HTShapeKey,
        itemGroup: HTMaterialContentGroup<HTShapeKey, Item>,
    ) {
        itemGroup.getOrNull(materialKey, shapeKey)?.run {
            // Smelting Recipe
            HTRuntimeDataRegistry.addRecipe(
                HTCookingRecipeBuilder()
                    .input { Ingredient.fromTag(HTShapeKeys.RAW_CHUNK.get().getItemTag(materialKey)) }
                    .output { this.defaultStack }
                    .buildSmelting(shapeKey.get().getId(materialKey)),
            )
            // Blasting Recipe
            HTRuntimeDataRegistry.addRecipe(
                HTCookingRecipeBuilder()
                    .input { Ingredient.fromTag(HTShapeKeys.RAW_CHUNK.get().getItemTag(materialKey)) }
                    .output { this.defaultStack }
                    .buildBlasting(shapeKey.get().getId(materialKey)),
            )
        }
    }

    // 1x Chunk -> 1x Gem
    private fun registerChunkGrindingRecipe(
        materialKey: HTMaterialKey,
        shapeKey: HTShapeKey,
        itemGroup: HTMaterialContentGroup<HTShapeKey, Item>,
    ) {
        itemGroup.getOrNull(materialKey, shapeKey)?.run {
            // Grinding
            HTRuntimeDataRegistry.addRecipe(
                HTGrindingRecipe(
                    shapeKey.get().getId(materialKey).prefix("grinding/"),
                    Ingredient.fromTag(HTShapeKeys.RAW_CHUNK.get().getItemTag(materialKey)),
                    this.defaultStack,
                ),
            )
        }
    }

    // Block -> 4/9x Ingots/Gems
    /*private fun registerBlockDecomposeRecipe(materialKey: HTMaterialKey, itemGroup: HTMaterialContentGroup<HTShapeKey, Item>, count: Int) {
        val defaultShape: HTShapeKey = materialKey.get()[HTMaterialProperties.DEFAULT_ITEM_SHAPE]
            ?: return
        val output: Item = itemGroup.getOrNull(materialKey, defaultShape) ?: return
        HTRuntimeDataRegistry.addRecipe(
            HTShapelessRecipeBuilder()
                .output { ItemStack(output, count) }
                .inputs(1) {
                    set(0, Ingredient.fromTag(HTShapeKeys.BLOCK.get().getItemTag(materialKey)))
                }
                .build(defaultShape.get().getId(materialKey)),
        )
    }

    // 4/9x Ingots/Gems -> Block
    private fun registerBlockConstructRecipe(materialKey: HTMaterialKey, material: HTPropertyHolder, recipeProperty: HTMaterialStorage) {
        val block: Block = recipeProperty.block
        val defaultShape: HTShapeKey = material[HTMaterialProperties.DEFAULT_ITEM_SHAPE]
            ?: return
        HTRuntimeDataRegistry.addRecipe(
            HTShapedRecipeBuilder()
                .inputs(3, 3) {
                    (0 until recipeProperty.count).forEach {
                        set(it, Ingredient.fromTag(defaultShape.get().getItemTag(materialKey)))
                    }
                }
                .output { ItemStack(block) }
                .build(HTShapeKeys.BLOCK.get().getId(materialKey)),
        )
    }*/

    /*@Suppress("UnstableApiUsage")
    @Environment(EnvType.CLIENT)
    private fun afterMaterialRegisteredOnClient(instance: HTMaterialsAPI) {
        // Block
        instance.materialContentManager.blockGroup.forEach { materialKey: HTMaterialKey, shapeKey: HTShapeKey, block: Block ->
            val material: HTPropertyHolder = materialKey.get()
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
            material.getOrDefault(
                HTMaterialProperties.blockState(shapeKey),
                Function {
                    VariantsBlockStateSupplier.create(
                        it,
                        BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(it)),
                    )
                },
            ).let { HTRuntimeClientPack.addBlockState(block, it::apply) }
            // Block Model
            material[HTMaterialProperties.blockModel(shapeKey)]
                ?.let { HTRuntimeClientPack.addBlockModel(block, it::accept) }
            // RenderLayer
            material[HTMaterialProperties.blockLayer(shapeKey)]
                ?.let { BlockRenderLayerMap.INSTANCE.putBlock(block, it) }
        }
        // Fluid
        instance.materialContentManager.fluidGroup.forEach { materialKey: HTMaterialKey, fluidPhase: HTFluidPhase, fluid: Fluid ->
            // Render
            val color: Int = materialKey.get().getOrDefault(HTMaterialProperties.COLOR, HTColor.WHITE).rgb
            FluidRenderHandlerRegistry.INSTANCE.register(
                fluid,
                HTSimpleFluidRenderHandler(
                    fluidPhase.textureId,
                    fluidPhase.textureId,
                    color,
                ),
            )
            FluidVariantRendering.register(
                fluid,
                HTMaterialFluidVariantRenderHandler(materialKey, fluidPhase),
            )
        }
        // Item
        instance.materialContentManager.itemGroup.forEach { materialKey: HTMaterialKey, shapeKey: HTShapeKey, item: Item ->
            val material: HTPropertyHolder = materialKey.get()
            // Color
            material[HTMaterialProperties.COLOR]?.rgb?.let { color ->
                ColorProviderRegistry.ITEM.register(
                    material.getOrDefault(
                        HTMaterialProperties.itemColor(shapeKey),
                        ItemColorProvider { _, tintIndex -> if (tintIndex == 0) color else -1 },
                    ),
                    item,
                )
            }
            // Model
            material.getOrDefault(
                HTMaterialProperties.itemModel(shapeKey),
                HTItemModelProperty.ofSimple(shapeKey),
            ).let { HTRuntimeClientPack.addItemModel(item) { builder, _ -> it.accept(builder) } }
        }
    }*/

    override fun bindMaterialWithFluid(builder: HTMaterialFluidManager.Builder) {
        super.bindMaterialWithFluid(builder)
        // Register vanilla fluids
        builder.add(HTMaterialKeys.WATER, HTFluidPhase.LIQUID, Fluids.WATER)
        builder.add(HTMaterialKeys.LAVA, HTFluidPhase.LIQUID, Fluids.LAVA)
        // Register fluids from content registry
        HTApiHolder.Material.apiInstance.materialContentManager.fluidGroup.forEach { materialKey, fluidPhase, fluid ->
            builder.add(materialKey, fluidPhase, fluid)
        }
    }

    override fun bindMaterialWithItem(builder: HTMaterialItemManager.Builder) {
        super.bindMaterialWithItem(builder)
        // Register vanilla items
        registerVanillaItems(builder)
        // Register items from content registry
        HTApiHolder.Material.apiInstance.materialContentManager.itemGroup.forEach { materialKey, shapeKey, item ->
            builder.add(materialKey, shapeKey, item)
        }
    }

    private fun registerVanillaItems(builder: HTMaterialItemManager.Builder) {
        // Andesite
        builder.add(HTMaterialKeys.ANDESITE, HTShapeKeys.BLOCK, Items.ANDESITE)
        builder.add(HTMaterialKeys.ANDESITE, HTShapeKeys.BLOCK, Items.POLISHED_ANDESITE)
        // Basalt
        builder.add(HTMaterialKeys.BASALT, HTShapeKeys.BLOCK, Items.BASALT)
        builder.add(HTMaterialKeys.BASALT, HTShapeKeys.BLOCK, Items.POLISHED_BASALT)
        // Blackstone
        builder.add(HTMaterialKeys.BLACKSTONE, HTShapeKeys.BLOCK, Items.BLACKSTONE)
        builder.add(HTMaterialKeys.BLACKSTONE, HTShapeKeys.BLOCK, Items.POLISHED_BLACKSTONE)
        builder.add(HTMaterialKeys.BLACKSTONE, HTShapeKeys.BRICKS, Items.CHISELED_POLISHED_BLACKSTONE)
        builder.add(HTMaterialKeys.BLACKSTONE, HTShapeKeys.BRICKS, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS)
        builder.add(HTMaterialKeys.BLACKSTONE, HTShapeKeys.BRICKS, Items.POLISHED_BLACKSTONE_BRICKS)
        // Brick
        builder.add(HTMaterialKeys.BRICK, HTShapeKeys.BRICKS, Items.BRICKS)
        builder.add(HTMaterialKeys.BRICK, HTShapeKeys.INGOT, Items.BRICK)
        // Charcoal
        builder.add(HTMaterialKeys.CHARCOAL, HTShapeKeys.GEM, Items.CHARCOAL)
        // Clay
        builder.add(HTMaterialKeys.CLAY, HTShapeKeys.BLOCK, Items.CLAY)
        builder.add(HTMaterialKeys.CLAY, HTShapeKeys.GEM, Items.CLAY_BALL)
        // Coal
        builder.add(HTMaterialKeys.COAL, HTShapeKeys.GEM, Items.COAL)
        builder.add(HTMaterialKeys.COAL, HTShapeKeys.BLOCK, Items.COAL_BLOCK)
        // Diamond
        builder.add(HTMaterialKeys.DIAMOND, HTShapeKeys.BLOCK, Items.DIAMOND_BLOCK)
        builder.add(HTMaterialKeys.DIAMOND, HTShapeKeys.GEM, Items.DIAMOND)
        builder.add(HTMaterialKeys.DIAMOND, HTShapeKeys.ORE, Items.DIAMOND_ORE)
        // Diorite
        builder.add(HTMaterialKeys.DIORITE, HTShapeKeys.BLOCK, Items.DIORITE)
        builder.add(HTMaterialKeys.DIORITE, HTShapeKeys.BLOCK, Items.POLISHED_DIORITE)
        // Dripstone
        // builder.add(HTMaterialKeys.DRIPSTONE, HTShapeKeys.BLOCK, Items.DRIPSTONE_BLOCK)
        // Emerald
        builder.add(HTMaterialKeys.EMERALD, HTShapeKeys.BLOCK, Items.EMERALD_BLOCK)
        builder.add(HTMaterialKeys.EMERALD, HTShapeKeys.GEM, Items.EMERALD)
        builder.add(HTMaterialKeys.EMERALD, HTShapeKeys.ORE, Items.EMERALD_ORE)
        // End Stone
        builder.add(HTMaterialKeys.END_STONE, HTShapeKeys.BLOCK, Items.END_STONE)
        builder.add(HTMaterialKeys.END_STONE, HTShapeKeys.BRICKS, Items.END_STONE_BRICKS)
        // Ender Pearl
        builder.add(HTMaterialKeys.ENDER_PEARL, HTShapeKeys.GEM, Items.ENDER_PEARL)
        // Flint
        builder.add(HTMaterialKeys.FLINT, HTShapeKeys.GEM, Items.FLINT)
        // Iron
        builder.add(HTMaterialKeys.IRON, HTShapeKeys.BLOCK, Items.IRON_BLOCK)
        builder.add(HTMaterialKeys.IRON, HTShapeKeys.INGOT, Items.IRON_INGOT)
        builder.add(HTMaterialKeys.IRON, HTShapeKeys.NUGGET, Items.IRON_NUGGET)
        builder.add(HTMaterialKeys.IRON, HTShapeKeys.ORE, Items.IRON_ORE)
        // Glass
        builder.add(HTMaterialKeys.GLASS, HTShapeKeys.BLOCK, Items.GLASS)
        // Glowstone
        builder.add(HTMaterialKeys.GLOWSTONE, HTShapeKeys.BLOCK, Items.GLOWSTONE)
        builder.add(HTMaterialKeys.GLOWSTONE, HTShapeKeys.DUST, Items.GLOWSTONE_DUST)
        // Gold
        builder.add(HTMaterialKeys.GOLD, HTShapeKeys.BLOCK, Items.GOLD_BLOCK)
        builder.add(HTMaterialKeys.GOLD, HTShapeKeys.INGOT, Items.GOLD_INGOT)
        builder.add(HTMaterialKeys.GOLD, HTShapeKeys.NUGGET, Items.GOLD_NUGGET)
        builder.add(HTMaterialKeys.GOLD, HTShapeKeys.ORE, Items.GOLD_ORE)
        // Granite
        builder.add(HTMaterialKeys.GRANITE, HTShapeKeys.BLOCK, Items.GRANITE)
        builder.add(HTMaterialKeys.GRANITE, HTShapeKeys.BLOCK, Items.POLISHED_GRANITE)
        // Lapis
        builder.add(HTMaterialKeys.LAPIS, HTShapeKeys.BLOCK, Items.LAPIS_BLOCK)
        builder.add(HTMaterialKeys.LAPIS, HTShapeKeys.GEM, Items.LAPIS_LAZULI)
        builder.add(HTMaterialKeys.LAPIS, HTShapeKeys.ORE, Items.LAPIS_ORE)
        // Nether Brick
        builder.add(HTMaterialKeys.NETHER_BRICK, HTShapeKeys.BRICKS, Items.NETHER_BRICKS)
        builder.add(HTMaterialKeys.NETHER_BRICK, HTShapeKeys.INGOT, Items.CHISELED_NETHER_BRICKS)
        builder.add(HTMaterialKeys.NETHER_BRICK, HTShapeKeys.INGOT, Items.CRACKED_NETHER_BRICKS)
        builder.add(HTMaterialKeys.NETHER_BRICK, HTShapeKeys.INGOT, Items.NETHER_BRICK)
        // Netherite
        builder.add(HTMaterialKeys.NETHERITE, HTShapeKeys.BLOCK, Items.NETHERITE_BLOCK)
        builder.add(HTMaterialKeys.NETHERITE, HTShapeKeys.INGOT, Items.NETHERITE_INGOT)
        // Netherrack
        builder.add(HTMaterialKeys.NETHERRACK, HTShapeKeys.BLOCK, Items.NETHERRACK)
        // Obsidian
        builder.add(HTMaterialKeys.OBSIDIAN, HTShapeKeys.BLOCK, Items.OBSIDIAN)
        // Quartz
        builder.add(HTMaterialKeys.QUARTZ, HTShapeKeys.BLOCK, Items.CHISELED_QUARTZ_BLOCK)
        builder.add(HTMaterialKeys.QUARTZ, HTShapeKeys.BLOCK, Items.QUARTZ_BLOCK)
        builder.add(HTMaterialKeys.QUARTZ, HTShapeKeys.BLOCK, Items.QUARTZ_PILLAR)
        builder.add(HTMaterialKeys.QUARTZ, HTShapeKeys.BLOCK, Items.SMOOTH_QUARTZ)
        builder.add(HTMaterialKeys.QUARTZ, HTShapeKeys.GEM, Items.QUARTZ)
        builder.add(HTMaterialKeys.QUARTZ, HTShapeKeys.ORE, Items.NETHER_QUARTZ_ORE)
        // Redstone
        builder.add(HTMaterialKeys.REDSTONE, HTShapeKeys.BLOCK, Items.REDSTONE_BLOCK)
        builder.add(HTMaterialKeys.REDSTONE, HTShapeKeys.DUST, Items.REDSTONE)
        builder.add(HTMaterialKeys.REDSTONE, HTShapeKeys.ORE, Items.REDSTONE_ORE)
        // Stone
        builder.add(HTMaterialKeys.STONE, HTShapeKeys.BLOCK, Items.SMOOTH_STONE)
        builder.add(HTMaterialKeys.STONE, HTShapeKeys.BLOCK, Items.STONE, false)
        builder.add(HTMaterialKeys.STONE, HTShapeKeys.BRICKS, ItemTags.STONE_BRICKS)
        // Wood
        builder.add(HTMaterialKeys.WOOD, HTShapeKeys.LOG, ItemTags.LOGS, false)
        builder.add(HTMaterialKeys.WOOD, HTShapeKeys.PLANKS, ItemTags.PLANKS, false)
    }
}
