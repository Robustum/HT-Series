package io.github.hiiragi283.api.material.content

import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.HTMaterial
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.resource.recipe.HTShapedRecipeBuilder
import io.github.hiiragi283.api.resource.recipe.HTShapelessRecipeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Material
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.Tag
import net.minecraft.text.MutableText
import net.minecraft.block.Block as MCBlock

object HTMaterialStorage {
    private fun buildSettings(type: Type, miningLevel: Int): FabricBlockSettings = FabricBlockSettings.of(type.material)
        .strength(5.0f, 5.0f)
        .sounds(type.soundCategory)
        .apply {
            if (miningLevel > 0) {
                requiresTool()
                breakByTool(type.miningTool, miningLevel)
            } else {
                breakByHand(true)
            }
        }

    //    Block    //

    class Block(type: Type, miningLevel: Int = 1) : MCBlock(buildSettings(type, miningLevel)) {
        override fun getName(): MutableText = asItem().name as MutableText
    }

    //    Recipe Property    //

    @Suppress("DataClassPrivateConstructor")
    data class RecipeProperty private constructor(
        val material: HTMaterial,
        val count: Int,
    ) {
        companion object {
            @JvmStatic
            fun ofFour(material: HTMaterial): RecipeProperty = RecipeProperty(material, 4)

            @JvmStatic
            fun ofNine(material: HTMaterial): RecipeProperty = RecipeProperty(material, 9)
        }

        val decomposeRecipe: ShapelessRecipe?
            get() {
                val defaultShape: HTShapeKey =
                    material[HTMaterialProperties.DEFAULT_ITEM_SHAPE] ?: return null
                val output: Item = material.getItem(defaultShape) ?: return null
                return HTShapelessRecipeBuilder()
                    .output { ItemStack(output, count) }
                    .inputs(1) {
                        set(0, Ingredient.fromTag(HTShapeKeys.BLOCK.get().getItemTag(material)))
                    }
                    .build(defaultShape.get().getId(material))
            }

        val constructRecipe: ShapedRecipe?
            get() {
                val defaultShape: HTShapeKey =
                    material[HTMaterialProperties.DEFAULT_ITEM_SHAPE] ?: return null
                val block: MCBlock = material.getBlock(HTShapeKeys.BLOCK) ?: return null
                return HTShapedRecipeBuilder()
                    .inputs(3, 3) {
                        (0 until count).forEach {
                            set(it, Ingredient.fromTag(defaultShape.get().getItemTag(material)))
                        }
                    }
                    .output { ItemStack(block) }
                    .build(HTShapeKeys.BLOCK.get().getId(material))
            }
    }

    //    Type    //

    enum class Type(
        val modelName: String,
        val material: Material,
        val soundCategory: BlockSoundGroup,
        val miningTool: Tag<Item>,
    ) {
        DEFAULT("solid", Material.STONE, BlockSoundGroup.STONE, FabricToolTags.PICKAXES),
        GEM("gem", Material.GLASS, BlockSoundGroup.GLASS, FabricToolTags.PICKAXES),
        METAL_DULL("dull", Material.METAL, BlockSoundGroup.METAL, FabricToolTags.PICKAXES),
        METAL_SHINY("shiny", Material.METAL, BlockSoundGroup.METAL, FabricToolTags.PICKAXES),
        WOOD("solid", Material.WOOD, BlockSoundGroup.WOOD, FabricToolTags.AXES),
    }
}
