package io.github.hiiragi283.engineering.compat

import io.github.hiiragi283.api.energy.HTEnergyLevel
import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialKeys
import io.github.hiiragi283.api.module.HTMaterialsAPI
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.module.HTPlugin
import io.github.hiiragi283.api.recipe.HTRecipe
import io.github.hiiragi283.api.resource.HTRuntimeDataRegistry
import io.github.hiiragi283.api.resource.recipe.HTShapedRecipeBuilder
import io.github.hiiragi283.engineering.common.init.HEItems
import io.github.hiiragi283.engineering.common.init.HERecipeTypes
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient

object HEHTPlugin : HTPlugin.Material {
    override val modId: String = HTModuleType.ENGINEERING.modId
    override val priority: Int = -90

    override fun afterMaterialRegistration(instance: HTMaterialsAPI, isClient: Boolean) {
        registerCraftingRecipes()
        registerCauldronRecipes()
        registerBlastingFurnaceRecipes()
    }

    private fun getMaterialItem(materialKey: HTMaterialKey, shapeKey: HTShapeKey): Item = checkNotNull(materialKey.get().getItem(shapeKey))

    private fun registerCraftingRecipes() {
        HTRuntimeDataRegistry.addRecipes(
            HTShapedRecipeBuilder()
                .inputs(1, 2) {
                    set(0, Ingredient.ofItems(Items.BRICK))
                    set(1, Ingredient.ofItems(Items.TERRACOTTA))
                }
                .output { HEItems.BRICK_WRAPPED_TERRACOTTA.defaultStack }
                .build(HTModuleType.ENGINEERING.id("brick_wrapped_terracotta")),
            HTShapedRecipeBuilder()
                .inputs(2, 1) {
                    set(0, Ingredient.ofItems(Items.SMOOTH_STONE))
                    set(1, Ingredient.fromTag(HTShapeKeys.PLATE.get().getItemTag(HTMaterialKeys.STEEl)))
                }
                .output { HEItems.STEEL_PLATED_STONE.defaultStack }
                .build(HTModuleType.ENGINEERING.id("steel_plated_stone")),
            HTShapedRecipeBuilder()
                .inputs(1, 2) {
                    set(0, Ingredient.fromTag(HTShapeKeys.PLATE.get().getItemTag(HTMaterialKeys.STEEl)))
                    set(1, Ingredient.ofItems(Items.SMOOTH_STONE))
                }
                .output { HEItems.STEEL_WRAPPED_STONE.defaultStack }
                .build(HTModuleType.ENGINEERING.id("steel_wrapped_stone")),
        )
    }

    private fun registerCauldronRecipes() {
        HTRuntimeDataRegistry.addRecipes(
            HTRecipe.Builder()
                .setInput(0, Items.BUCKET, 1)
                .setInput(1, Items.ICE, 1)
                .setOutput(0, Items.WATER_BUCKET, 1)
                .setRequiredEnergy(HTEnergyType.HEAT, HTEnergyLevel.LOW)
                .build(HTModuleType.ENGINEERING.id("cauldron_test"), HERecipeTypes.CAULDRON),
        )
    }

    private fun registerBlastingFurnaceRecipes() {
        val builder = HTRecipe.Builder()
            .setInput(0, HTShapeKeys.DUST.get().getItemTag(HTMaterialKeys.RAW_STEEl), 1)
            .setOutput(0, getMaterialItem(HTMaterialKeys.STEEl, HTShapeKeys.INGOT), 1)
            .setOutput(1, getMaterialItem(HTMaterialKeys.SLAG, HTShapeKeys.DUST), 1)
            .setRequiredEnergy(HTEnergyType.HEAT, HTEnergyLevel.MEDIUM)
        HTRuntimeDataRegistry.addRecipes(
            builder
                .setInput(1, HTShapeKeys.DUST.get().getItemTag(HTMaterialKeys.CHARCOAL), 2)
                .build(HTModuleType.ENGINEERING.id("raw_steel"), HERecipeTypes.PRIMITIVE_BLAST_FURNACE),
            builder
                .setInput(1, HTShapeKeys.DUST.get().getItemTag(HTMaterialKeys.COKE), 1)
                .build(HTModuleType.ENGINEERING.id("raw_steel1"), HERecipeTypes.PRIMITIVE_BLAST_FURNACE),
        )
    }
}
