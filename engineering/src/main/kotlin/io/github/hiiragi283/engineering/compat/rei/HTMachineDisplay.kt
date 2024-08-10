package io.github.hiiragi283.engineering.compat.rei

import io.github.hiiragi283.api.extension.toEntryStack
import io.github.hiiragi283.api.recipe.HTIngredient
import io.github.hiiragi283.api.recipe.HTRecipe
import io.github.hiiragi283.api.recipe.HTResult
import io.github.hiiragi283.material.compat.rei.HMREIPlugin
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeDisplay
import net.minecraft.util.Identifier
import java.util.*

class HTMachineDisplay private constructor(
    private val inputs: List<List<EntryStack>>,
    private val output: List<List<EntryStack>>,
    private val display: HTRecipe?,
) : RecipeDisplay {
    constructor(recipe: HTRecipe) : this(
        recipe.ingredients.map(HTIngredient::toEntryStack),
        recipe.results.map(HTResult::toEntryStack),
        recipe,
    )

    override fun getRecipeLocation(): Optional<Identifier> = Optional.ofNullable(display).map(HTRecipe::getId)

    override fun getInputEntries(): List<List<EntryStack>> = inputs

    override fun getResultingEntries(): List<List<EntryStack>> = output

    override fun getRecipeCategory(): Identifier = HMREIPlugin.GRINDING

    override fun getRequiredEntries(): List<List<EntryStack>> = inputEntries
}
