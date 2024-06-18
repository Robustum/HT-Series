package io.github.hiiragi283.api.resource

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import io.github.hiiragi283.api.extension.addProperty
import io.github.hiiragi283.api.extension.buildJson
import io.github.hiiragi283.api.extension.toJsonArray
import io.github.hiiragi283.api.extension.toJsonObject
import net.minecraft.block.Block
import net.minecraft.client.render.model.json.JsonUnbakedModel
import net.minecraft.client.render.model.json.ModelElement
import net.minecraft.client.render.model.json.ModelOverride
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.data.client.model.ModelIds
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.util.Identifier

class HTModelJsonBuilder {
    companion object {
        private val GSON = GsonBuilder().setPrettyPrinting().create()

        @JvmStatic
        fun simpleItemModel(builder: HTModelJsonBuilder, item: Item) {
            simpleItemModel(builder, ModelIds.getItemModelId(item))
        }

        @JvmOverloads
        @JvmStatic
        fun simpleItemModel(builder: HTModelJsonBuilder, layer0: Identifier, layer1: Identifier? = null) {
            builder.parentId = Identifier("item/generated")
            builder.addTexture("layer0", layer0)
            layer1?.let { builder.addTexture("layer1", it) }
        }

        @JvmStatic
        fun blockParented(builder: HTModelJsonBuilder, blockItem: BlockItem) {
            blockParented(builder, blockItem.block)
        }

        @JvmStatic
        fun blockParented(builder: HTModelJsonBuilder, block: Block) {
            builder.parentId = ModelIds.getBlockModelId(block)
        }
    }

    var parentId: Identifier? = null
    val elements: MutableList<ModelElement> = mutableListOf()
    private val textureMap: MutableMap<String, Identifier> = mutableMapOf()
    var ambientOcclusion: Boolean = true
    var guiLight: JsonUnbakedModel.GuiLight? = null
    var transformation: ModelTransformation? = null
    var overrides: List<ModelOverride>? = null

    fun addTexture(key: String, texId: String) {
        addTexture(key, Identifier(texId))
    }

    fun addTexture(key: String, texId: Identifier) {
        textureMap[key] = texId
    }

    fun removeTexture(key: String) {
        textureMap.remove(key)
    }

    fun toJson(): JsonObject = buildJson {
        // parent
        parentId?.let { addProperty("parent", it) }
        // elements
        add("elements", elements.toJsonArray(GSON::toJsonTree))
        // texture map
        add("textures", textureMap.toJsonObject { JsonPrimitive(it.toString()) })
        // ambientocclusion
        addProperty("ambientocclusion", ambientOcclusion)
        // gui_light
        guiLight?.let { addProperty("gui_light", it.toString()) }
        // display
        transformation?.let(GSON::toJsonTree)?.let { add("display", it) }
        // overrides
        overrides?.toJsonArray(GSON::toJsonTree)?.let { add("overrides", it) }
    }
}
