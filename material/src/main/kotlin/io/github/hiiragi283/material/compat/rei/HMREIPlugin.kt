package io.github.hiiragi283.material.compat.rei

import io.github.hiiragi283.api.compat.rei.HTMaterialEntryStack
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.recipe.HTGrindingRecipe
import io.github.hiiragi283.material.impl.HTMaterialsAPIImpl
import me.shedaniel.rei.api.EntryRegistry
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeHelper
import me.shedaniel.rei.api.plugins.REIPluginV0
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Blocks
import net.minecraft.util.Identifier

@Environment(EnvType.CLIENT)
object HMREIPlugin : REIPluginV0 {
    @JvmField
    val MATERIAL: Identifier = HTModuleType.MATERIAL.id("material")

    @JvmField
    val GRINDING = HTModuleType.MATERIAL.id("grinding")

    override fun getPluginIdentifier(): Identifier = HTModuleType.MATERIAL.id("plugin")

    override fun registerEntries(entryRegistry: EntryRegistry) {
        HTApiHolder.Material.apiInstance.materialRegistry.values
            .map(::HTMaterialEntryStack)
            .forEach(entryRegistry::registerEntry)
    }

    override fun registerPluginCategories(recipeHelper: RecipeHelper) {
        recipeHelper.registerCategory(HTMaterialCategory)
        recipeHelper.registerCategory(HTGrindingCategory)
    }

    override fun registerRecipeDisplays(recipeHelper: RecipeHelper) {
        HTApiHolder.Material.apiInstance.materialRegistry.values
            .map(::HTMaterialDisplay)
            .filterNot { it.entries.isEmpty() }
            .forEach(recipeHelper::registerDisplay)
        recipeHelper.registerRecipes(GRINDING, HTGrindingRecipe::class.java, ::HTGrindingDisplay)
    }

    override fun registerOthers(recipeHelper: RecipeHelper) {
        recipeHelper.registerWorkingStations(MATERIAL, EntryStack.create(HTMaterialsAPIImpl.dictionaryItem))
        recipeHelper.registerWorkingStations(GRINDING, EntryStack.create(Blocks.GRINDSTONE))

        recipeHelper.removeAutoCraftButton(MATERIAL)
        recipeHelper.removeAutoCraftButton(GRINDING)
    }
}
