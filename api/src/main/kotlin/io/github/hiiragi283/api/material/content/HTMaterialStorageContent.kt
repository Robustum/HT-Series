package io.github.hiiragi283.api.material.content

import io.github.hiiragi283.api.extension.singleBlockStateFunction
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.material.type.HTBlockProperty
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.property.HTPropertyHolder
import io.github.hiiragi283.api.recipe.builder.HTShapedRecipeBuilder
import io.github.hiiragi283.api.recipe.builder.HTShapelessRecipeBuilder
import io.github.hiiragi283.api.resource.HTModelJsonBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.data.client.model.BlockStateSupplier
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.text.MutableText
import net.minecraft.util.Identifier

class HTMaterialStorageContent private constructor(
    val materialKey: HTMaterialKey,
    val property: HTBlockProperty,
    val count: Int,
    miningLevel: Int,
) {
    companion object {
        @JvmStatic
        fun createFour(materialKey: HTMaterialKey, type: HTBlockProperty, miningLevel: Int): HTMaterialStorageContent =
            HTMaterialStorageContent(materialKey, type, 4, miningLevel)

        @JvmStatic
        fun createNine(materialKey: HTMaterialKey, type: HTBlockProperty, miningLevel: Int): HTMaterialStorageContent =
            HTMaterialStorageContent(materialKey, type, 9, miningLevel)
    }

    private val contentManager: HTMaterialContentManager by lazy { HTApiHolder.Material.apiInstance.materialContentManager }
    private val material: HTPropertyHolder by lazy { materialKey.get() }

    private val settings: FabricBlockSettings = buildSettings(property, miningLevel)
    val block: Block = object : Block(settings) {
        override fun getName(): MutableText = asItem().name as MutableText
    }

    val decomposeRecipe: ShapelessRecipe?
        get() {
            val defaultShape: HTShapeKey =
                material[HTMaterialProperties.DEFAULT_ITEM_SHAPE] ?: return null
            val output: Item = contentManager.itemGroup.getOrNull(materialKey, defaultShape) ?: return null
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

    private val blockModelId: Identifier = HTModuleType.API.id("block/storage/${property.modelName}")

    val blockStateFunction: (Block) -> BlockStateSupplier =
        singleBlockStateFunction(blockModelId)

    val itemModelFunction: (HTModelJsonBuilder, Item) -> Unit = { builder, _ ->
        builder.parentId = blockModelId
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
