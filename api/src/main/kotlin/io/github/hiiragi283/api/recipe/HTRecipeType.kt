package io.github.hiiragi283.api.recipe

import com.google.gson.JsonObject
import io.github.hiiragi283.api.energy.HTEnergyLevel
import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.extension.checkNotNull
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry

interface HTRecipeType : RecipeType<HTRecipe>, RecipeSerializer<HTRecipe> {
    val name: String

    fun icon(): ItemStack

    override fun read(id: Identifier, json: JsonObject): HTRecipe {
        val typeName: String = JsonHelper.getString(json, "type")
        val type: HTRecipeType = typeName
            .let(::Identifier)
            .let(Registry.RECIPE_SERIALIZER::get)
            ?.let { it as? HTRecipeType }
            .checkNotNull { "Could not find HTRecipeType named $typeName" }
        val ingredients: List<HTIngredient> = JsonHelper.getArray(json, "ingredients")
            .map { HTIngredient.fromJson(it.asJsonObject) }
        val results: List<HTResult> = JsonHelper.getArray(json, "results")
            .map { HTResult.fromJson(it.asJsonObject) }
        val requiredEnergies: Map<HTEnergyType, HTEnergyLevel> = JsonHelper.getObject(json, "energy")
            .entrySet()
            .associate { HTEnergyType.valueOf(it.key) to HTEnergyLevel.valueOf(it.value.asString) }
        return HTRecipe(
            id,
            type,
            ingredients,
            results,
            requiredEnergies,
        )
    }

    override fun read(id: Identifier, buf: PacketByteBuf): HTRecipe {
        TODO("Not yet implemented")
    }

    override fun write(buf: PacketByteBuf, recipe: HTRecipe) {
        TODO("Not yet implemented")
    }
}
