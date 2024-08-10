package io.github.hiiragi283.api.resource

import io.github.hiiragi283.api.extension.buildLootPool
import io.github.hiiragi283.api.extension.buildLootTable
import io.github.hiiragi283.api.extension.rolls
import io.github.hiiragi283.api.extension.surviveExplosion
import net.minecraft.block.Block
import net.minecraft.data.server.recipe.RecipeJsonProvider
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.recipe.Recipe
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer

object HTRuntimeDataRegistry {
    //    Advancement    //

    //    Loot Table    //

    @JvmStatic
    val lootTables: Map<Identifier, LootTable.Builder>
        get() = lootTables1
    private val lootTables1: MutableMap<Identifier, LootTable.Builder> = ConcurrentHashMap()

    @JvmStatic
    fun addLootTable(id: Identifier, builder: LootTable.Builder) {
        lootTables1[id] = builder
    }

    @JvmOverloads
    @JvmStatic
    fun <T : Block> addBlockLootTable(
        block: T,
        builderAction: (T) -> LootTable.Builder = {
            buildLootTable {
                buildLootPool {
                    rolls(1)
                    with(ItemEntry.builder(it))
                    surviveExplosion()
                }
            }
        },
    ) {
        addLootTable(block.lootTableId, builderAction(block))
    }

    //    Recipe    //

    val recipes: Map<Identifier, Recipe<*>>
        get() = recipes1
    private val recipes1: MutableMap<Identifier, Recipe<*>> = ConcurrentHashMap()

    @JvmStatic
    fun addRecipes(vararg recipes: Recipe<*>) {
        recipes.forEach(::addRecipe)
    }

    @JvmStatic
    fun addRecipe(recipe: Recipe<*>) {
        recipes1[recipe.id] = recipe
    }

    @JvmField
    val EXPORTER: Consumer<RecipeJsonProvider> = Consumer { provider ->
        addRecipe(provider.serializer.read(provider.recipeId, provider.toJson()))
    }

    //    Tag    //

    internal val blockTags: MutableMap<Identifier, MutableList<Tag.Entry>> = mutableMapOf()
    internal val fluidTags: MutableMap<Identifier, MutableList<Tag.Entry>> = mutableMapOf()
    internal val itemTags: MutableMap<Identifier, MutableList<Tag.Entry>> = mutableMapOf()
    internal val entityTypeTags: MutableMap<Identifier, MutableList<Tag.Entry>> = mutableMapOf()

    fun addBlockTag(id: Identifier, entry: Tag.Entry) {
        blockTags.computeIfAbsent(id) { mutableListOf() }.add(entry)
    }

    fun addFluidTag(id: Identifier, entry: Tag.Entry) {
        fluidTags.computeIfAbsent(id) { mutableListOf() }.add(entry)
    }

    fun addItemTag(id: Identifier, entry: Tag.Entry) {
        itemTags.computeIfAbsent(id) { mutableListOf() }.add(entry)
    }

    fun addEntityTypeTag(id: Identifier, entry: Tag.Entry) {
        entityTypeTags.computeIfAbsent(id) { mutableListOf() }.add(entry)
    }
}
