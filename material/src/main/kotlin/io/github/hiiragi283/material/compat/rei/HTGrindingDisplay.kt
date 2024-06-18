package io.github.hiiragi283.material.compat.rei

import io.github.hiiragi283.api.recipe.HTGrindingRecipe
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeDisplay
import net.minecraft.util.Identifier
import org.jetbrains.annotations.ApiStatus
import java.util.*

@ApiStatus.Experimental
class HTGrindingDisplay(
    private val inputs: List<List<EntryStack>>,
    private val output: List<List<EntryStack>>,
) : RecipeDisplay {
    private var display: HTGrindingRecipe? = null

    constructor(recipe: HTGrindingRecipe) : this(
        EntryStack.ofIngredients(recipe.ingredients),
        listOf(listOf(EntryStack.create(recipe.output))),
    ) {
        display = recipe
    }

    override fun getRecipeLocation(): Optional<Identifier> = Optional.ofNullable(display).map(HTGrindingRecipe::getId)

    override fun getInputEntries(): List<List<EntryStack>> = inputs

    override fun getResultingEntries(): List<List<EntryStack>> = output

    override fun getRecipeCategory(): Identifier = HMREIPlugin.GRINDING

    override fun getRequiredEntries(): List<List<EntryStack>> = inputEntries
}
