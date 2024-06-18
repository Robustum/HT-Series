package io.github.hiiragi283.engineering.common.init

import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.recipe.HTRecipeType
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object HERecipeTypes {
    @JvmField
    val CAULDRON: HTRecipeType =
        registerRecipeType("cauldron", HEItems.CAULDRON::getDefaultStack)

    @JvmField
    val PRIMITIVE_BLAST_FURNACE: HTRecipeType =
        registerRecipeType("primitive_blast_furnace", HEItems.PRIMITIVE_BLAST_FURNACE::getDefaultStack)

    private fun registerRecipeType(path: String, icon: () -> ItemStack): HTRecipeType {
        val id: Identifier = HTModuleType.ENGINEERING.id(path)
        val type = HTRecipeType(id, icon)
        Registry.register(Registry.RECIPE_TYPE, id, type)
        Registry.register(Registry.RECIPE_SERIALIZER, id, type)
        return type
    }
}
