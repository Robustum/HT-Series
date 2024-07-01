package io.github.hiiragi283.api.recipe

import io.github.hiiragi283.api.block.entity.HTEnergySourceFinder
import io.github.hiiragi283.api.energy.HTEnergyLevel
import io.github.hiiragi283.api.energy.HTEnergyManager
import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.extension.prefix
import net.minecraft.inventory.Inventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeSerializer
import net.minecraft.recipe.RecipeType
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.world.World

data class HTRecipe(
    @JvmField val id: Identifier,
    @JvmField val type: HTRecipeType,
    @JvmField val ingredients: List<HTIngredient>,
    @JvmField val results: List<HTResult>,
    @JvmField val requiredEnergies: Map<HTEnergyType, HTEnergyLevel>,
) : Recipe<Inventory> {
    override fun matches(inventory: Inventory, world: World): Boolean {
        ingredients.forEachIndexed { index, ingredient ->
            if (!ingredient.test(inventory.getStack(index))) return false
        }
        if (inventory is HTEnergySourceFinder) {
            requiredEnergies.forEach { (type: HTEnergyType, minLevel: HTEnergyLevel) ->
                val checkEnergy: Boolean = inventory.getValidSide(type).directions.any { direction: Direction ->
                    HTEnergyManager.getLevelOrOff(
                        type,
                        world,
                        inventory.pos().offset(direction),
                        direction.opposite,
                    ) >= minLevel
                }
                if (!checkEnergy) return false
            }
        }
        return true
    }

    private fun canInsert(inventory: Inventory, world: World): Boolean {
        results.forEachIndexed { index, result ->
            val stackIn: ItemStack = inventory.getStack(index + 3)
            if (!result.test(stackIn)) return false
        }
        return true
    }

    fun process(inventory: Inventory, world: World) {
        if (!matches(inventory, world)) return
        if (!canInsert(inventory, world)) return
        results.forEachIndexed { index, result ->
            val outputIndex: Int = index + 3
            val stackIn: ItemStack = inventory.getStack(outputIndex)
            inventory.setStack(outputIndex, result.apply(stackIn.copy()))
        }
        ingredients.forEachIndexed { index, ingredient ->
            ingredient.accept(inventory.getStack(index))
        }
    }

    override fun craft(inventory: Inventory): ItemStack = output

    override fun fits(width: Int, height: Int): Boolean = true

    override fun getOutput(): ItemStack = results
        .getOrNull(0)
        ?.previewStacks
        ?.getOrNull(0)
        ?: ItemStack.EMPTY

    override fun createIcon(): ItemStack = type.icon()

    override fun getId(): Identifier = id

    override fun getSerializer(): RecipeSerializer<*> = type

    override fun getType(): RecipeType<*> = type

    //    Builder    //

    class Builder {
        private val inputs: MutableList<HTIngredient> = mutableListOf()
        private val outputs: MutableList<HTResult> = mutableListOf()
        private val requiredEnergies: MutableMap<HTEnergyType, HTEnergyLevel> = mutableMapOf()

        fun setInput(index: Int, input: HTIngredient): Builder = apply {
            if (inputs.getOrNull(index) == null) {
                inputs.add(index, input)
            } else {
                inputs[index] = input
            }
        }

        fun setInput(index: Int, item: Item, count: Int): Builder = setInput(index, HTIngredient.ItemImpl(item, count))

        fun setInput(index: Int, tag: Tag<Item>, count: Int): Builder = setInput(index, HTIngredient.TagImpl(tag, count))

        fun setOutput(index: Int, result: HTResult): Builder = apply {
            if (outputs.getOrNull(index) == null) {
                outputs.add(index, result)
            } else {
                outputs[index] = result
            }
        }

        fun setOutput(index: Int, item: Item, count: Int): Builder = setOutput(index, HTResult.ItemImpl(item, count))

        fun setRequiredEnergy(type: HTEnergyType, minLevel: HTEnergyLevel) = apply {
            requiredEnergies[type] = minLevel
        }

        fun build(id: Identifier, type: HTRecipeType): HTRecipe = HTRecipe(
            id.prefix("${type.typeId.path}/"),
            type,
            inputs.toList(),
            outputs.toList(),
            requiredEnergies.toMap(),
        )
    }
}
