package io.github.hiiragi283.api.material.content

import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.material.type.HTBlockProperty
import io.github.hiiragi283.api.material.type.HTMaterialType
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.property.HTPropertyHolder
import io.github.hiiragi283.api.resource.recipe.HTShapedRecipeBuilder
import io.github.hiiragi283.api.resource.recipe.HTShapelessRecipeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.text.MutableText

class HTMaterialStorage private constructor(
    val materialKey: HTMaterialKey,
    val property: HTBlockProperty,
    val modelName: String,
    val miningLevel: Int,
    val count: Int,
) {
    companion object {
        @JvmStatic
        fun createFour(materialKey: HTMaterialKey, type: HTBlockProperty, miningLevel: Int) =
            HTMaterialStorage(materialKey, type, getModelName(type), miningLevel, 4)

        @JvmStatic
        fun createNine(materialKey: HTMaterialKey, type: HTBlockProperty, miningLevel: Int) =
            HTMaterialStorage(materialKey, type, getModelName(type), miningLevel, 9)

        private fun getModelName(property: HTBlockProperty): String = when (property) {
            is HTMaterialType.Gem -> "gem"
            is HTMaterialType.Metal -> if (property.isShiny) "shiny" else "dull"
            else -> "solid"
        }
    }

    private val contentManager: HTMaterialContentManager by lazy { HTApiHolder.Material.apiInstance.materialContentManager }
    private val material: HTPropertyHolder by lazy { materialKey.get() }

    private val settings = buildSettings(property, miningLevel)
    val block: Block = object : Block(settings) {
        override fun getName(): MutableText = asItem().name as MutableText
    }

    val decomposeRecipe: ShapelessRecipe?
        get() {
            val defaultShape: HTShapeKey =
                material[HTMaterialProperties.DEFAULT_ITEM_SHAPE] ?: return null
            val output: Item = contentManager.itemGroup.get(materialKey, defaultShape) ?: return null
            return HTShapelessRecipeBuilder()
                .output { ItemStack(output, count) }
                .inputs(1) {
                    set(0, Ingredient.fromTag(HTShapeKeys.BLOCK.get().getItemTag(materialKey)))
                }
                .build(defaultShape.get().getId(materialKey))
        }

    val constructRecipe: ShapedRecipe?
        get() {
            val defaultShape: HTShapeKey =
                material[HTMaterialProperties.DEFAULT_ITEM_SHAPE] ?: return null
            return HTShapedRecipeBuilder()
                .inputs(3, 3) {
                    (0 until count).forEach {
                        set(it, Ingredient.fromTag(defaultShape.get().getItemTag(materialKey)))
                    }
                }
                .output { ItemStack(block) }
                .build(HTShapeKeys.BLOCK.get().getId(materialKey))
        }

    init {
        check(miningLevel >= 0) { "Mining level of storage block must be 0 or more!" }
    }

    private fun buildSettings(property: HTBlockProperty, miningLevel: Int): FabricBlockSettings =
        FabricBlockSettings.of(property.blockMaterial)
            .strength(5.0f, 5.0f)
            .sounds(property.soundGroup)
            .apply {
                if (miningLevel > 0) {
                    requiresTool()
                    breakByTool(property.miningTool, miningLevel)
                } else {
                    breakByHand(true)
                }
            }
}
