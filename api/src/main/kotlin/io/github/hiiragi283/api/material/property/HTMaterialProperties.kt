package io.github.hiiragi283.api.material.property

import io.github.hiiragi283.api.extension.TypedIdentifier
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.material.composition.HTElement
import io.github.hiiragi283.api.material.content.HTMaterialOre
import io.github.hiiragi283.api.material.content.HTMaterialStorageContent
import io.github.hiiragi283.api.material.type.HTMaterialType
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.resource.HTModelJsonBuilder
import net.minecraft.block.Block
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.data.client.model.BlockStateSupplier
import net.minecraft.item.Item
import net.minecraft.loot.LootTable
import java.awt.Color

object HTMaterialProperties {
    //    Static    //

    @JvmField
    val TYPE: TypedIdentifier<HTMaterialType> =
        TypedIdentifier.of(HTModuleType.API.id("type"))

    @JvmField
    val COLOR: TypedIdentifier<Color> =
        TypedIdentifier.of(HTModuleType.API.id("color"))

    @JvmField
    val FORMULA: TypedIdentifier<String> =
        TypedIdentifier.of(HTModuleType.API.id("formula"))

    @JvmField
    val MOLAR: TypedIdentifier<Double> =
        TypedIdentifier.of(HTModuleType.API.id("molar"))

    @JvmField
    val COMPONENT: TypedIdentifier<Map<HTElement, Int>> =
        TypedIdentifier.of(HTModuleType.API.id("component"))

    @JvmField
    val BLOCK_SET: TypedIdentifier<Set<HTShapeKey>> =
        TypedIdentifier.of(HTModuleType.API.id("block_set"))

    @JvmField
    val FLUID_SET: TypedIdentifier<Set<HTFluidPhase>> =
        TypedIdentifier.of(HTModuleType.API.id("fluid_set"))

    @JvmField
    val ITEM_SET: TypedIdentifier<Set<HTShapeKey>> =
        TypedIdentifier.of(HTModuleType.API.id("item_set"))

    @JvmField
    val DEFAULT_ITEM_SHAPE: TypedIdentifier<HTShapeKey> =
        TypedIdentifier.of(HTModuleType.API.id("default_item_shape"))

    @JvmField
    val EXPLOSION: TypedIdentifier<HTExplosionProperty> =
        TypedIdentifier.of(HTModuleType.API.id("explosion"))

    @JvmField
    val STORAGE: TypedIdentifier<HTMaterialStorageContent> =
        TypedIdentifier.of(HTModuleType.API.id("storage"))

    @JvmField
    val ORE: TypedIdentifier<HTMaterialOre> =
        TypedIdentifier.of(HTModuleType.API.id("ore"))

    //    Dynamic    //

    @JvmStatic
    fun blockColor(shapeKey: HTShapeKey): TypedIdentifier<BlockColorProvider> =
        TypedIdentifier.of(HTModuleType.API.id("block_color/${shapeKey.name}"))

    @JvmStatic
    fun blockLayer(shapeKey: HTShapeKey): TypedIdentifier<RenderLayer> =
        TypedIdentifier.of(HTModuleType.API.id("block_layer/${shapeKey.name}"))

    @JvmStatic
    fun blockLoot(shapeKey: HTShapeKey): TypedIdentifier<(Block) -> LootTable.Builder> =
        TypedIdentifier.of(HTModuleType.API.id("block_loot/${shapeKey.name}"))

    @JvmStatic
    fun blockModel(shapeKey: HTShapeKey): TypedIdentifier<(HTModelJsonBuilder, Block) -> Unit> =
        TypedIdentifier.of(HTModuleType.API.id("block_model/${shapeKey.name}"))

    @JvmStatic
    fun blockState(shapeKey: HTShapeKey): TypedIdentifier<(Block) -> BlockStateSupplier> =
        TypedIdentifier.of(HTModuleType.API.id("blockstate/${shapeKey.name}"))

    @JvmStatic
    fun itemBlockModel(shapeKey: HTShapeKey): TypedIdentifier<(HTModelJsonBuilder, Item) -> Unit> =
        TypedIdentifier.of(HTModuleType.API.id("item_block_model/${shapeKey.name}"))

    @JvmStatic
    fun itemColor(shapeKey: HTShapeKey): TypedIdentifier<ItemColorProvider> =
        TypedIdentifier.of(HTModuleType.API.id("item_color/${shapeKey.name}"))

    /*@JvmStatic
    fun itemModel(shapeKey: HTShapeKey): TypedIdentifier<Consumer<HTModelJsonBuilder>> =
        TypedIdentifier.of(HTModuleType.API.id("item_model/${shapeKey.name}"))*/
}
