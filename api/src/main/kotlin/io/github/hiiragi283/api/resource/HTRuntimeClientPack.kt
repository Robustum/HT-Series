package io.github.hiiragi283.api.resource

import com.google.gson.JsonElement
import io.github.hiiragi283.api.extension.modify
import io.github.hiiragi283.api.module.HTModuleType
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.block.Block
import net.minecraft.data.client.model.*
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.resource.ResourcePack
import net.minecraft.resource.ResourcePackProfile
import net.minecraft.resource.ResourcePackProvider
import net.minecraft.resource.ResourceType
import net.minecraft.resource.metadata.PackResourceMetadata
import net.minecraft.resource.metadata.PackResourceMetadataReader
import net.minecraft.resource.metadata.ResourceMetadataReader
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.function.Consumer
import java.util.function.Predicate

object HTRuntimeClientPack : ResourcePack {
    private val domains: MutableSet<String> = hashSetOf()

    @JvmStatic
    fun addDomains(vararg domains: String) {
        domains.forEach(HTRuntimeClientPack.domains::add)
    }

    private val clientData: MutableMap<Identifier, JsonElement> = hashMapOf()
    private val serverData: MutableMap<Identifier, JsonElement> = hashMapOf()

    private fun getData(type: ResourceType) = when (type) {
        ResourceType.CLIENT_RESOURCES -> clientData
        ResourceType.SERVER_DATA -> serverData
    }

    @JvmStatic
    fun addData(
        type: ResourceType,
        id: Identifier,
        prefix: String,
        json: JsonElement,
    ) {
        addDomains(id.namespace)
        getData(type)[id.modify { "$prefix/$it.json" }] = json
    }

    //    Client Data    //

    @Environment(EnvType.CLIENT)
    @JvmStatic
    fun addSimpleBlockState(block: Block) {
        addBlockState(block) {
            VariantsBlockStateSupplier.create(
                it,
                BlockStateVariant.create()
                    .put(VariantSettings.MODEL, ModelIds.getBlockModelId(it)),
            )
        }
    }

    @Environment(EnvType.CLIENT)
    @JvmStatic
    fun <T : Block> addBlockState(block: T, blockStateSupplier: (T) -> BlockStateSupplier) {
        addBlockState(block, blockStateSupplier(block).get())
    }

    @Environment(EnvType.CLIENT)
    @JvmStatic
    fun addBlockState(block: Block, blockStateSupplier: BlockStateSupplier) {
        addBlockState(block, blockStateSupplier.get())
    }

    private fun addBlockState(block: Block, json: JsonElement) {
        addData(ResourceType.CLIENT_RESOURCES, Registry.BLOCK.getId(block), "blockstates", json)
    }

    /*@Environment(EnvType.CLIENT)
    @JvmStatic
    fun addModel(item: Item, texture: Texture = Texture.layer0(item), model: Model = Models.GENERATED) {
        model.upload(ModelIds.getItemModelId(item).modify { "models/$it.json" }, texture) { id, supplier ->
            addDomains(id.namespace)
            addClientData(id, supplier.get())
        }
    }*/

    @Environment(EnvType.CLIENT)
    @JvmStatic
    fun <T : Block> addBlockModel(block: T, builderAction: (HTModelJsonBuilder, T) -> Unit) {
        addModel(ModelIds.getBlockModelId(block), HTModelJsonBuilder().apply { builderAction(this, block) }.toJson())
    }

    @Environment(EnvType.CLIENT)
    @JvmStatic
    fun <T : Item> addItemModel(item: T, builderAction: (HTModelJsonBuilder, T) -> Unit) {
        addModel(ModelIds.getItemModelId(item), HTModelJsonBuilder().apply { builderAction(this, item) }.toJson())
    }

    @Environment(EnvType.CLIENT)
    @JvmStatic
    fun <T : BlockItem> addBlockItemModel(item: T, builderAction: (HTModelJsonBuilder, Block) -> Unit) {
        addModel(ModelIds.getItemModelId(item), HTModelJsonBuilder().apply { builderAction(this, item.block) }.toJson())
    }

    private fun addModel(id: Identifier, json: JsonElement) {
        addData(ResourceType.CLIENT_RESOURCES, id, "models", json)
    }

    //    Server Data    //

    /*@JvmStatic
    fun addAdvancement(id: Identifier, task: Advancement.Task) {
        addData(ResourceType.SERVER_DATA, id, "advancements", task.toJson())
    }

    @JvmStatic
    fun addSimpleBlockLooTable(block: Block) {
        addBlockLootTable(block) {
            LootTable.Builder()
                .type(LootContextTypes.BLOCK)
                .pool(
                    LootPool.builder()
                        .rolls(ConstantLootTableRange.create(1))
                        .with(ItemEntry.builder(block))
                        .conditionally(SurvivesExplosionLootCondition.builder()),
                )
        }
    }

    @JvmStatic
    fun <T : Block> addBlockLootTable(block: T, builder: (T) -> LootTable.Builder) {
        addData(ResourceType.SERVER_DATA, block.lootTableId, "loot_tables", LootManager.toJson(builder(block).build()))
    }

    @Deprecated("")
    @JvmStatic
    fun addRecipe(consumer: Consumer<Consumer<RecipeJsonProvider>>) {
        consumer.accept { provider ->
            addRecipe(provider.recipeId, provider.toJson())
        }
    }

    @JvmStatic
    fun addRecipe(id: Identifier, jsonElement: JsonElement) {
        addData(ResourceType.SERVER_DATA, id, "recipes", jsonElement)
    }*/

    //    ResourcePack    //

    override fun close() = Unit

    @Throws(IOException::class)
    override fun openRoot(fileName: String): InputStream = throw IOException("Cannot invoke openRoot!")

    @Throws(IOException::class)
    override fun open(type: ResourceType, id: Identifier): InputStream? = getData(type)[id]
        ?.let(JsonElement::toString)
        ?.let(String::toByteArray)
        ?.let(::ByteArrayInputStream)

    override fun findResources(
        type: ResourceType,
        namespace: String,
        prefix: String,
        maxDepth: Int,
        pathFilter: Predicate<String>,
    ): Collection<Identifier> = if (namespace !in domains) {
        setOf()
    } else {
        getData(type).keys
            .filter { id: Identifier -> id.path.startsWith(prefix) }
            .filter { id: Identifier -> pathFilter.test(id.path) }
    }

    override fun contains(type: ResourceType, id: Identifier): Boolean = id in getData(type)

    override fun getNamespaces(type: ResourceType): Set<String> = domains

    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class)
    override fun <T> parseMetadata(metaReader: ResourceMetadataReader<T>): T? =
        PackResourceMetadata(LiteralText(name), 6).takeIf { metaReader is PackResourceMetadataReader } as? T

    override fun getName(): String = "${HTModuleType.MATERIAL.modName}'s Runtime Pack"

    //    Provider    //

    object Provider : ResourcePackProvider {
        override fun register(profileAdder: Consumer<ResourcePackProfile>, factory: ResourcePackProfile.Factory) {
            ResourcePackProfile.of(
                HTRuntimeClientPack.name,
                true,
                { HTRuntimeClientPack },
                factory,
                ResourcePackProfile.InsertionPosition.TOP,
                { it },
            )?.let(profileAdder::accept)
        }
    }
}
