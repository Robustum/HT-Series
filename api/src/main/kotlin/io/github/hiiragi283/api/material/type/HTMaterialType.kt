package io.github.hiiragi283.api.material.type

import io.github.hiiragi283.api.extension.lowerName
import io.github.hiiragi283.api.extension.suffix
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.module.HTModuleType
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Material
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier

sealed interface HTMaterialType {
    val blockSet: Set<HTShapeKey>
    val fluidSet: Set<HTFluidPhase>
    val itemSet: Set<HTShapeKey>

    fun layer0(shapeKey: HTShapeKey): Identifier = HTModuleType.API.id("item/$shapeKey")

    fun layer1(shapeKey: HTShapeKey): Identifier? = layer0(shapeKey).suffix("_overlay")

    enum class Fluid(override val fluidSet: Set<HTFluidPhase>) : HTMaterialType {
        POWDER(HTFluidPhase.POWDER),
        LIQUID(HTFluidPhase.LIQUID),
        GAS(HTFluidPhase.GAS),
        PLASMA(HTFluidPhase.PLASMA),
        ;

        constructor(phase: HTFluidPhase) : this(setOf(phase))

        override val blockSet: Set<HTShapeKey> = emptySet()
        override val itemSet: Set<HTShapeKey> = emptySet()
    }

    enum class Gem(val hasOverlay: Boolean) : HTMaterialType, HTBlockProperty {
        AMETHYST(true),
        COAL(false),
        DIAMOND(true),
        EMERALD(true),
        FLINT(false),
        LAPIS(false),
        QUARTZ(true),
        RUBY(true),
        ;

        override val blockSet: Set<HTShapeKey> = setOf(
            HTShapeKeys.BLOCK,
            HTShapeKeys.ORE,
            HTShapeKeys.ORE_BLACKSTONE,
            HTShapeKeys.ORE_END,
            HTShapeKeys.ORE_GRAVEL,
            HTShapeKeys.ORE_NETHER,
            HTShapeKeys.ORE_SAND,
        )
        override val fluidSet: Set<HTFluidPhase> = emptySet()
        override val itemSet: Set<HTShapeKey> = setOf(
            HTShapeKeys.DUST,
            HTShapeKeys.GEAR,
            HTShapeKeys.GEM,
            HTShapeKeys.PLATE,
            HTShapeKeys.RAW_CHUNK,
            HTShapeKeys.ROD,
        )

        override fun layer0(shapeKey: HTShapeKey): Identifier = when (shapeKey) {
            HTShapeKeys.GEM -> HTModuleType.API.id("item/gem_$lowerName")
            else -> super.layer0(shapeKey)
        }

        override fun layer1(shapeKey: HTShapeKey): Identifier? = super.layer1(shapeKey)?.takeIf { hasOverlay }

        //    HTBlockProperty    //

        override val blockMaterial: Material = Material.GLASS
        override val soundGroup: BlockSoundGroup = BlockSoundGroup.GLASS
        override val miningTool: Tag<Item>? by lazy { FabricToolTags.PICKAXES }
        override val modelName: String = "gem"
    }

    enum class Metal(val isShiny: Boolean) : HTMaterialType, HTBlockProperty {
        SHINY(true),
        DULL(false),
        ;

        override val blockSet: Set<HTShapeKey> = setOf(
            HTShapeKeys.BLOCK,
            HTShapeKeys.ORE,
            HTShapeKeys.ORE_BLACKSTONE,
            HTShapeKeys.ORE_END,
            HTShapeKeys.ORE_GRAVEL,
            HTShapeKeys.ORE_NETHER,
            HTShapeKeys.ORE_SAND,
        )
        override val fluidSet: Set<HTFluidPhase> = setOf(HTFluidPhase.LIQUID)
        override val itemSet: Set<HTShapeKey> = setOf(
            HTShapeKeys.DUST,
            HTShapeKeys.GEAR,
            HTShapeKeys.INGOT,
            HTShapeKeys.NUGGET,
            HTShapeKeys.PLATE,
            HTShapeKeys.RAW_CHUNK,
            HTShapeKeys.ROD,
        )

        override fun layer1(shapeKey: HTShapeKey): Identifier? = super.layer1(shapeKey)?.takeIf { isShiny }

        //    HTBlockProperty    //

        override val blockMaterial: Material = Material.METAL
        override val soundGroup: BlockSoundGroup = BlockSoundGroup.METAL
        override val miningTool: Tag<Item>? by lazy { FabricToolTags.PICKAXES }
        override val modelName: String = when (isShiny) {
            true -> "shiny"
            false -> "dull"
        }

        companion object {
            @JvmStatic
            fun fromBoolean(isShiny: Boolean): Metal = when (isShiny) {
                true -> SHINY
                false -> DULL
            }
        }
    }

    data object Solid : HTMaterialType, HTBlockProperty {
        override val blockSet: Set<HTShapeKey> = emptySet()
        override val fluidSet: Set<HTFluidPhase> = emptySet()
        override val itemSet: Set<HTShapeKey> = setOf(HTShapeKeys.DUST)

        //    HTBlockProperty    //

        override val blockMaterial: Material = Material.STONE
        override val soundGroup: BlockSoundGroup = BlockSoundGroup.STONE
        override val miningTool: Tag<Item>? by lazy { FabricToolTags.PICKAXES }
        override val modelName: String = "solid"
    }

    data object Wood : HTMaterialType {
        override val blockSet: Set<HTShapeKey> = emptySet()
        override val fluidSet: Set<HTFluidPhase> = emptySet()
        override val itemSet: Set<HTShapeKey> = setOf(
            HTShapeKeys.DUST,
            HTShapeKeys.GEAR,
            HTShapeKeys.PLATE,
        )

        override fun layer1(shapeKey: HTShapeKey): Identifier? = null
    }
}
