package io.github.hiiragi283.api.resource.recipe

import io.github.hiiragi283.api.extension.prefix
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.SmithingRecipe
import net.minecraft.util.Identifier

class HTSmithingRecipeBuilder {
    private lateinit var base: Ingredient

    fun base(ingredient: Ingredient): HTSmithingRecipeBuilder = apply {
        base = ingredient
    }

    private lateinit var addition: Ingredient

    fun addition(ingredient: Ingredient): HTSmithingRecipeBuilder = apply {
        addition = ingredient
    }

    private lateinit var output: ItemStack

    fun output(supplier: () -> ItemStack): HTSmithingRecipeBuilder = apply {
        val stack: ItemStack = supplier()
        check(!stack.isEmpty) { "Output ItemStack cannot be empty!" }
        this.output = stack
    }

    fun build(id: Identifier): SmithingRecipe = SmithingRecipe(id.prefix("smithing/"), base, addition, output)
}
