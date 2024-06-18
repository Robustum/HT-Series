package io.github.hiiragi283.api.material.property

import io.github.hiiragi283.api.block.HTMaterialStorageBlock
import io.github.hiiragi283.api.extension.TypedIdentifier
import io.github.hiiragi283.api.extension.lowerName
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.composition.HTElement
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.resource.HTModelJsonBuilder
import net.minecraft.block.Block
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.client.color.item.ItemColorProvider
import net.minecraft.client.render.RenderLayer
import net.minecraft.data.client.model.BlockStateSupplier
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.loot.LootTable
import java.awt.Color
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function

object HTMaterialProperties {
    //    Static    //

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
    val CAN_GRIND_CHUNK: TypedIdentifier<Boolean> =
        TypedIdentifier.of(HTModuleType.API.id("can_grind_chunk"))

    @JvmField
    val CAN_SMELT_CHUNK: TypedIdentifier<Boolean> =
        TypedIdentifier.of(HTModuleType.API.id("can_smelt_chunk"))

    @JvmField
    val CRUCIBLE_RESULT: TypedIdentifier<HTMaterialKey> =
        TypedIdentifier.of(HTModuleType.API.id("crucible_result"))

    @JvmField
    val DEFAULT_ITEM_SHAPE: TypedIdentifier<HTShapeKey> =
        TypedIdentifier.of(HTModuleType.API.id("default_item_shape"))

    @JvmField
    val EXPLOSION: TypedIdentifier<HTExplosionProperty> =
        TypedIdentifier.of(HTModuleType.API.id("explosion"))

    @JvmField
    val STORAGE_BLOCK_RECIPE: TypedIdentifier<HTStorageBlockRecipe> =
        TypedIdentifier.of(HTModuleType.API.id("storage_block_recipe"))

    @JvmField
    val STORAGE_BLOCK_TYPE: TypedIdentifier<HTMaterialStorageBlock.Type> =
        TypedIdentifier.of(HTModuleType.API.id("storage_block_type"))

    //    Dynamic    //

    @JvmStatic
    fun blockContent(shapeKey: HTShapeKey): TypedIdentifier<Lazy<Block>> = TypedIdentifier.of(HTModuleType.API.id("block/${shapeKey.name}"))

    @JvmStatic
    fun blockColor(shapeKey: HTShapeKey): TypedIdentifier<BlockColorProvider> =
        TypedIdentifier.of(HTModuleType.API.id("block_color/${shapeKey.name}"))

    @JvmStatic
    fun blockLayer(shapeKey: HTShapeKey): TypedIdentifier<RenderLayer> =
        TypedIdentifier.of(HTModuleType.API.id("block_layer/${shapeKey.name}"))

    @JvmStatic
    fun blockLoot(shapeKey: HTShapeKey): TypedIdentifier<Function<Block, LootTable.Builder>> =
        TypedIdentifier.of(HTModuleType.API.id("block_loot/${shapeKey.name}"))

    @JvmStatic
    fun blockModel(shapeKey: HTShapeKey): TypedIdentifier<BiConsumer<HTModelJsonBuilder, Block>> =
        TypedIdentifier.of(HTModuleType.API.id("block_model/${shapeKey.name}"))

    @JvmStatic
    fun blockState(shapeKey: HTShapeKey): TypedIdentifier<Function<Block, BlockStateSupplier>> =
        TypedIdentifier.of(HTModuleType.API.id("blockstate/${shapeKey.name}"))

    @JvmStatic
    fun fluidContent(phase: HTFluidPhase): TypedIdentifier<Lazy<Fluid>> =
        TypedIdentifier.of(HTModuleType.API.id("fluid/${phase.lowerName}"))

    @JvmStatic
    fun itemContent(shapeKey: HTShapeKey): TypedIdentifier<Lazy<Item>> = TypedIdentifier.of(HTModuleType.API.id("item/${shapeKey.name}"))

    @JvmStatic
    fun itemColor(shapeKey: HTShapeKey): TypedIdentifier<ItemColorProvider> =
        TypedIdentifier.of(HTModuleType.API.id("item_color/${shapeKey.name}"))

    @JvmStatic
    fun itemModel(shapeKey: HTShapeKey): TypedIdentifier<Consumer<HTModelJsonBuilder>> =
        TypedIdentifier.of(HTModuleType.API.id("item_model/${shapeKey.name}"))
}
