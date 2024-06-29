package io.github.hiiragi283.api.material.content

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import io.github.hiiragi283.api.extension.buildLootPool
import io.github.hiiragi283.api.extension.buildLootTable
import io.github.hiiragi283.api.extension.checkNotNull
import io.github.hiiragi283.api.extension.rolls
import io.github.hiiragi283.api.extension.withFortune
import io.github.hiiragi283.api.extension.withSilkTouch
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.item.shape.HTShapedMaterial
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.property.HTConfiguredFeatureProperty
import io.github.hiiragi283.api.module.HTApiHolder
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.data.client.model.ModelIds
import net.minecraft.item.Item
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.structure.rule.BlockMatchRuleTest
import net.minecraft.structure.rule.RuleTest
import net.minecraft.structure.rule.TagMatchRuleTest
import net.minecraft.tag.BlockTags
import net.minecraft.tag.Tag
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.decorator.Decorator
import net.minecraft.world.gen.decorator.RangeDecoratorConfig
import net.minecraft.world.gen.feature.ConfiguredFeature
import net.minecraft.world.gen.feature.Feature
import net.minecraft.world.gen.feature.OreFeatureConfig
import net.minecraft.block.Block as MCBlock

object HTMaterialOre {
    @JvmStatic
    fun createLootTable(block: MCBlock, materialKey: HTMaterialKey): LootTable.Builder = buildLootTable {
        buildLootPool {
            rolls(1)
            with(
                ItemEntry.builder(block)
                    .withSilkTouch(
                        ItemEntry.builder(
                            HTApiHolder.Material
                                .apiInstance
                                .materialItemManager
                                .getOrEmpty(materialKey, HTShapeKeys.RAW_CHUNK),
                        ).withFortune(),
                    ),
            )
        }
    }

    class Block(rock: Rock, miningLevel: Int) :
        MCBlock(FabricBlockSettings.copyOf(rock.parent).breakByTool(rock.toolTags, miningLevel)) {
        override fun getName(): MutableText = asItem().name as MutableText
    }

    enum class Type(val texId: String) {
        GEM("block/ore_gem"),
        METAL("block/ore_metal"),
    }

    enum class Rock(
        val parent: MCBlock,
        val toolTags: Tag<Item>,
        val shapeKey: HTShapeKey,
        val ruleTest: RuleTest,
    ) {
        STONE(Blocks.STONE, FabricToolTags.PICKAXES, HTShapeKeys.ORE, BlockTags.BASE_STONE_OVERWORLD),
        NETHER(Blocks.NETHERRACK, FabricToolTags.PICKAXES, HTShapeKeys.ORE_NETHER),
        BLACKSTONE(Blocks.BLACKSTONE, FabricToolTags.PICKAXES, HTShapeKeys.ORE_BLACKSTONE),
        END(Blocks.END_STONE, FabricToolTags.PICKAXES, HTShapeKeys.ORE_END),
        GRAVEL(Blocks.GRAVEL, FabricToolTags.SHOVELS, HTShapeKeys.ORE_GRAVEL),
        SAND(Blocks.SAND, FabricToolTags.SHOVELS, HTShapeKeys.ORE_SAND),
        ;

        constructor(
            parent: MCBlock,
            toolTags: Tag<Item>,
            shape: HTShapeKey,
            replacement: MCBlock = parent,
        ) : this(parent, toolTags, shape, BlockMatchRuleTest(replacement))

        constructor(
            parent: MCBlock,
            toolTags: Tag<Item>,
            shape: HTShapeKey,
            replacement: Tag<MCBlock>,
        ) : this(parent, toolTags, shape, TagMatchRuleTest(replacement))

        val texId: Identifier = ModelIds.getBlockModelId(parent)
    }

    object Manager {
        private val properties: Table<HTMaterialKey, Rock, FeatureProperty> =
            HashBasedTable.create()

        @JvmStatic
        fun getProperty(key: HTMaterialKey, rock: Rock): FeatureProperty? = properties.get(key, rock)

        @JvmStatic
        fun registerProperty(
            key: HTMaterialKey,
            rock: Rock,
            veinSize: VeinSize,
            height: Height,
            probability: Probability,
        ) {
            properties.put(key, rock, FeatureProperty(key, rock, veinSize, height, probability))
        }
    }

    data class FeatureProperty(
        val materialKey: HTMaterialKey,
        val rock: Rock,
        val veinSize: VeinSize,
        val height: Height,
        val probability: Probability,
    ) : HTConfiguredFeatureProperty {
        val shapeKey: HTShapeKey = rock.shapeKey

        override val configuredFeature: ConfiguredFeature<*, *> by lazy {
            val state: BlockState = HTApiHolder.Material
                .apiInstance
                .materialContentManager
                .blockGroup
                .getOrNull(materialKey, shapeKey)
                ?.defaultState
                .checkNotNull { "Could not find Ore Block ${HTShapedMaterial(materialKey, shapeKey)}!" }
            Feature.ORE.configure(
                OreFeatureConfig(rock.ruleTest, state, veinSize.size),
            ).decorate(
                Decorator.RANGE.configure(
                    RangeDecoratorConfig(height.min, 0, height.max),
                ),
            ).spreadHorizontally()
                .repeat(probability.perChunk)
        }

        override val registryKey: RegistryKey<ConfiguredFeature<*, *>> by lazy {
            shapeKey.get().getRegistryKey(Registry.CONFIGURED_FEATURE_WORLDGEN, materialKey)
        }

        override val step: GenerationStep.Feature = GenerationStep.Feature.UNDERGROUND_ORES

        override fun checkBiome(context: BiomeSelectionContext): Boolean = BiomeSelectors.all().test(context)
    }

    enum class VeinSize(val size: Int) {
        LARGE(16),
        MEDIUM(8),
        SMALL(4),
    }

    enum class Height(val min: Int, val max: Int) {
        BOTTOM(0..15),
        MIDDLE(0..31),
        COMMON(0..63),
        SURFACE(48..79),
        ALL(0..255),
        ;

        constructor(range: IntRange) : this(range.first, range.last)
    }

    enum class Probability(val perChunk: Int) {
        COMMON(8),
        RARE(4),
        VERY_RARE(1),
    }
}
