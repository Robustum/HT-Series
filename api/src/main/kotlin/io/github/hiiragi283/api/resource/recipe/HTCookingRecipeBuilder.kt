package io.github.hiiragi283.api.resource.recipe

import io.github.hiiragi283.api.extension.prefix
import net.minecraft.item.ItemStack
import net.minecraft.recipe.*
import net.minecraft.util.Identifier

class HTCookingRecipeBuilder {
    private lateinit var input: Ingredient

    fun input(supplier: () -> Ingredient): HTCookingRecipeBuilder = apply {
        input = supplier()
    }

    private lateinit var output: ItemStack

    fun output(supplier: () -> ItemStack): HTCookingRecipeBuilder = apply {
        val stack: ItemStack = supplier()
        check(!stack.isEmpty) { "Output ItemStack cannot be empty!" }
        this.output = stack
    }

    fun <T : AbstractCookingRecipe> build(
        id: Identifier,
        prefix: String,
        group: String = "",
        exp: Float,
        time: Int,
        builder: (Identifier, String, Ingredient, ItemStack, Float, Int) -> T,
    ): T = builder(id.prefix("$prefix/"), group, input, output, exp, time)

    fun buildBlasting(
        id: Identifier,
        group: String = "",
        exp: Float = 0.0f,
        time: Int = 100,
    ): BlastingRecipe = build(id, "blasting", group, exp, time, ::BlastingRecipe)

    fun buildSmelting(
        id: Identifier,
        group: String = "",
        exp: Float = 0.0f,
        time: Int = 200,
    ): SmeltingRecipe = build(id, "smelting", group, exp, time, ::SmeltingRecipe)

    fun buildCampfire(
        id: Identifier,
        group: String = "",
        exp: Float = 0.0f,
        time: Int = 100,
    ): CampfireCookingRecipe = build(id, "campfire", group, exp, time, ::CampfireCookingRecipe)

    fun buildSmoking(
        id: Identifier,
        group: String = "",
        exp: Float = 0.0f,
        time: Int = 100,
    ): SmokingRecipe = build(id, "smoking", group, exp, time, ::SmokingRecipe)
}
