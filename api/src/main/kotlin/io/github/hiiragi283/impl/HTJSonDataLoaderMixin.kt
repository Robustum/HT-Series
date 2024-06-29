package io.github.hiiragi283.impl

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.github.hiiragi283.api.event.HTTagEvents
import io.github.hiiragi283.api.extension.arrange
import io.github.hiiragi283.api.extension.runCatchAndLog
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.resource.HTRuntimeDataRegistry
import net.minecraft.loot.*
import net.minecraft.loot.condition.LootCondition
import net.minecraft.loot.condition.LootConditionManager
import net.minecraft.loot.context.LootContextType
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.RecipeType
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import java.io.BufferedReader
import java.io.InputStream

internal object HTJSonDataLoaderMixin {
    //    Loot Table    //

    private val LOOT_GSON: Gson = LootGsons.getTableGsonBuilder().create()

    @JvmStatic
    fun loadLootTables(map: MutableMap<Identifier, JsonElement>, conditionManager: LootConditionManager): Map<Identifier, LootTable> =
        buildMap {
            map.forEach { (id: Identifier, element: JsonElement) ->
                runCatchAndLog {
                    this[id] = LOOT_GSON.fromJson(element, LootTable::class.java)
                }
            }
            HTRuntimeDataRegistry.lootTables.forEach { (id: Identifier, builder: LootTable.Builder) ->
                runCatchAndLog {
                    this[id] = builder.build()
                }
            }
            put(LootTables.EMPTY, LootTable.EMPTY)
            // validate loot tables
            val contextType: LootContextType = LootContextTypes.GENERIC
            val function: (Identifier) -> LootCondition? = conditionManager::get
            val reporter = LootTableReporter(contextType, function, this::get)
            this.forEach { (id: Identifier, table: LootTable) ->
                LootManager.validate(reporter, id, table)
            }
            reporter.messages.forEach { key, value ->
                HTLogger.log { it.warn("Found validation problem in $key: $value") }
            }
        }

    //    Recipe    //

    @JvmStatic
    fun loadRecipes(map: Map<Identifier, JsonElement>): Map<RecipeType<*>, MutableMap<Identifier, Recipe<*>>> = buildMap {}

    //    Tag    //

    private val TAG_GSON = Gson()

    @JvmStatic
    fun loadTagMap(manager: ResourceManager, dataType: String): Map<Identifier, Tag.Builder> {
        val result: MutableMap<Identifier, Tag.Builder> = hashMapOf()
        manager.findResources(dataType) { it.endsWith(".json") }.forEach { id: Identifier ->
            val tagId: Identifier = id.arrange("$dataType/", ".json")
            manager.getAllResources(id).forEach { resource: Resource ->
                resource.use {
                    resource
                        .let(Resource::getInputStream)
                        .let(InputStream::bufferedReader)
                        .let { reader: BufferedReader ->
                            JsonHelper.deserialize(
                                TAG_GSON,
                                reader,
                                JsonObject::class.java,
                            )
                        }
                        ?.let { jsonObject: JsonObject ->
                            result.computeIfAbsent(tagId) { Tag.Builder.create() }
                                .read(jsonObject, resource.resourcePackName)
                        }
                }
            }
        }
        // Invoke TagsBuildingEvent
        HTLogger.log { it.info("Current loading tag type; $dataType") }
        when (dataType) {
            "tags/blocks" ->
                HTTagEvents.BLOCK.invoker().register(HTTagEvents.Build.Handler(result, Registry.BLOCK::getId))

            "tags/items" ->
                HTTagEvents.ITEM.invoker().register(HTTagEvents.Build.Handler(result, Registry.ITEM::getId))

            "tags/fluids" ->
                HTTagEvents.FLUID.invoker().register(HTTagEvents.Build.Handler(result, Registry.FLUID::getId))

            "tags/entity_types" ->
                HTTagEvents.ENTITY_TYPE.invoker()
                    .register(HTTagEvents.Build.Handler(result, Registry.ENTITY_TYPE::getId))

            else -> Unit
        }
        // Remove empty builder
        /*HashMap(result).forEach { (id: Identifier, builder: Tag.Builder) ->
            if (!builder.streamEntries().findAny().isPresent) {
                result.remove(id)
            }
        }
        HTMaterialsAPI.LOGGER.info("Removed empty tag builders!")*/
        return result
    }
}
