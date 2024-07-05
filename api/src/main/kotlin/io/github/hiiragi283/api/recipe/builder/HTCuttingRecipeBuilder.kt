package io.github.hiiragi283.api.recipe.builder

import io.github.hiiragi283.api.extension.prefix
import net.minecraft.item.ItemStack
import net.minecraft.recipe.CuttingRecipe
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.StonecuttingRecipe
import net.minecraft.util.Identifier

class HTCuttingRecipeBuilder {
    private lateinit var input: Ingredient

    fun input(supplier: () -> Ingredient): HTCuttingRecipeBuilder = apply {
        input = supplier()
    }

    private lateinit var output: ItemStack

    fun output(supplier: () -> ItemStack): HTCuttingRecipeBuilder = apply {
        val stack: ItemStack = supplier()
        check(!stack.isEmpty) { "Output ItemStack cannot be empty!" }
        this.output = stack
    }

    fun <T : CuttingRecipe> build(
        id: Identifier,
        prefix: String,
        group: String = "",
        builder: (Identifier, String, Ingredient, ItemStack) -> T,
    ): T = builder(id.prefix("$prefix/"), group, input, output)

    fun buildStoneCutting(id: Identifier, group: String = ""): StonecuttingRecipe = build(id, "stone_cut", group, ::StonecuttingRecipe)
}
