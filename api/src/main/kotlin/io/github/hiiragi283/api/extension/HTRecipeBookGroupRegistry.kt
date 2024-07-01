package io.github.hiiragi283.api.extension

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.recipebook.RecipeBookGroup
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType

@Environment(EnvType.CLIENT)
object HTRecipeBookGroupRegistry {
    private val registry: MutableMap<RecipeType<*>, (Recipe<*>) -> RecipeBookGroup> = mutableMapOf()

    init {
        register(RecipeType.CRAFTING) { recipe ->
            when (recipe.output.item.group) {
                ItemGroup.BUILDING_BLOCKS -> RecipeBookGroup.CRAFTING_BUILDING_BLOCKS
                ItemGroup.TOOLS -> RecipeBookGroup.CRAFTING_EQUIPMENT
                ItemGroup.COMBAT -> RecipeBookGroup.CRAFTING_EQUIPMENT
                ItemGroup.REDSTONE -> RecipeBookGroup.CRAFTING_REDSTONE
                else -> RecipeBookGroup.CRAFTING_MISC
            }
        }
        register(RecipeType.SMELTING) { recipe ->
            val output: ItemStack = recipe.output
            when {
                output.item.isFood -> RecipeBookGroup.FURNACE_FOOD
                output.item is BlockItem -> RecipeBookGroup.FURNACE_BLOCKS
                else -> RecipeBookGroup.FURNACE_MISC
            }
        }
        register(RecipeType.BLASTING) { recipe ->
            when {
                recipe.output.item is BlockItem -> RecipeBookGroup.BLAST_FURNACE_BLOCKS
                else -> RecipeBookGroup.BLAST_FURNACE_MISC
            }
        }
        register(RecipeType.SMOKING, RecipeBookGroup.SMOKER_FOOD)
        register(RecipeType.STONECUTTING, RecipeBookGroup.STONECUTTER)
        register(RecipeType.CAMPFIRE_COOKING, RecipeBookGroup.CAMPFIRE)
        register(RecipeType.SMITHING, RecipeBookGroup.SMITHING)
    }

    @JvmStatic
    fun register(type: RecipeType<*>, recipeBookGroup: RecipeBookGroup) {
        register(type) { recipeBookGroup }
    }

    @JvmStatic
    fun register(type: RecipeType<*>, function: (Recipe<*>) -> RecipeBookGroup) {
        registry[type] = function
    }

    @JvmStatic
    fun getGroup(recipe: Recipe<*>): RecipeBookGroup? = registry[recipe.type]?.invoke(recipe)

    @JvmStatic
    fun getGroupOrUnknown(recipe: Recipe<*>): RecipeBookGroup = getGroup(recipe) ?: RecipeBookGroup.UNKNOWN
}
