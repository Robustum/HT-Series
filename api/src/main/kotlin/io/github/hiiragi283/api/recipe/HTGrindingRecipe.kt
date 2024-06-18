package io.github.hiiragi283.api.recipe

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import io.github.hiiragi283.api.extension.decodeItemStack
import net.minecraft.block.Blocks
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.collection.DefaultedList
import net.minecraft.world.World
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Experimental
class HTGrindingRecipe(private val id: Identifier, private val ingredient: Ingredient, private val output: ItemStack) :
    Recipe<Inventory> {
    override fun matches(inventory: Inventory, world: World): Boolean = inventory.getStack(0).let { !it.isEmpty && ingredient.test(it) }

    override fun craft(inventory: Inventory): ItemStack = output.copy()

    override fun fits(width: Int, height: Int): Boolean = true

    override fun getOutput(): ItemStack = output

    override fun getIngredients(): DefaultedList<Ingredient> = DefaultedList.copyOf(Ingredient.EMPTY, ingredient)

    override fun createIcon(): ItemStack = ItemStack(Blocks.GRINDSTONE)

    override fun getId(): Identifier = id

    override fun getSerializer(): RecipeSerializer<*> = Serializer

    override fun getType(): RecipeType<*> = Type

    object Type : RecipeType<HTGrindingRecipe>

    object Serializer : RecipeSerializer<HTGrindingRecipe> {
        override fun read(id: Identifier, json: JsonObject): HTGrindingRecipe {
            val ingredient: Ingredient = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"))
            val output: ItemStack = decodeItemStack(
                JsonOps.INSTANCE,
                JsonHelper.getObject(json, "result"),
                true,
            ).getOrDefault(ItemStack.EMPTY)
            return HTGrindingRecipe(id, ingredient, output)
        }

        override fun read(id: Identifier, buf: PacketByteBuf): HTGrindingRecipe {
            val ingredient: Ingredient = Ingredient.fromPacket(buf)
            val output: ItemStack = buf.readItemStack()
            return HTGrindingRecipe(id, ingredient, output)
        }

        override fun write(buf: PacketByteBuf, recipe: HTGrindingRecipe) {
            recipe.ingredient.write(buf)
            buf.writeItemStack(recipe.output)
        }
    }
}
