package io.github.hiiragi283.api.resource.recipe

import io.github.hiiragi283.api.extension.buildDefaultedList
import io.github.hiiragi283.api.extension.prefix
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.ShapelessRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

class HTShapelessRecipeBuilder {
    private lateinit var inputs: DefaultedList<Ingredient>

    fun inputs(size: Int, builderAction: DefaultedList<Ingredient>.() -> Unit): HTShapelessRecipeBuilder = apply {
        inputs = buildDefaultedList(size, Ingredient.EMPTY, builderAction)
    }

    private lateinit var output: ItemStack

    fun output(supplier: () -> ItemStack): HTShapelessRecipeBuilder = apply {
        val stack: ItemStack = supplier()
        check(!stack.isEmpty) { "Output ItemStack cannot be empty!" }
        this.output = stack
    }

    fun build(id: Identifier, group: String = ""): ShapelessRecipe = ShapelessRecipe(id.prefix("shapeless/"), group, output, inputs)
}
