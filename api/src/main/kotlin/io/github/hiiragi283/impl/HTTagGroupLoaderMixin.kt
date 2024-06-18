package io.github.hiiragi283.impl

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.github.hiiragi283.api.event.HTTagEvents
import io.github.hiiragi283.api.extension.arrange
import io.github.hiiragi283.api.module.HTLogger
import net.minecraft.resource.Resource
import net.minecraft.resource.ResourceManager
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import java.io.BufferedReader
import java.io.InputStream

object HTTagGroupLoaderMixin {
    private val gson = Gson()

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
                        .let { reader: BufferedReader -> JsonHelper.deserialize(gson, reader, JsonObject::class.java) }
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
                HTTagEvents.ENTITY_TYPE.invoker().register(HTTagEvents.Build.Handler(result, Registry.ENTITY_TYPE::getId))

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
