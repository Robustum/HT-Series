package io.github.hiiragi283.api.resource.recipe

import io.github.hiiragi283.api.extension.buildDefaultedList
import io.github.hiiragi283.api.extension.prefix
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList

class HTShapedRecipeBuilder {
    private var width: Int = 1
    private var height: Int = 1
    private lateinit var inputs: DefaultedList<Ingredient>

    fun inputs(width: Int, height: Int, builderAction: DefaultedList<Ingredient>.() -> Unit): HTShapedRecipeBuilder = apply {
        check(width in 0..3) { "Recipe width must be from 0 to 3!" }
        check(height in 0..3) { "Recipe height must be from 0 to 3!" }
        this.width = width
        this.height = height
        inputs = buildDefaultedList(width * height, Ingredient.EMPTY, builderAction)
    }

    private lateinit var output: ItemStack

    fun output(supplier: () -> ItemStack): HTShapedRecipeBuilder = apply {
        val stack: ItemStack = supplier()
        check(!stack.isEmpty) { "Output ItemStack cannot be empty!" }
        this.output = stack
    }

    fun build(id: Identifier, group: String = ""): ShapedRecipe = ShapedRecipe(id.prefix("shaped/"), group, width, height, inputs, output)
}
