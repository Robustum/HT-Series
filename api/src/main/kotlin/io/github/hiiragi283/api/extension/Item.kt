package io.github.hiiragi283.api.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tag.Tag
import net.minecraft.util.registry.Registry

//    Item    //

fun createItem(settings: FabricItemSettings.() -> FabricItemSettings): Item = createItem(::Item, settings)

fun createBlockItem(block: Block, settings: FabricItemSettings.() -> FabricItemSettings): BlockItem =
    createBlockItem(block, ::BlockItem, settings)

fun <B : Block, I : Item> createBlockItem(
    block: B,
    constructor: (B, FabricItemSettings) -> I,
    settings: FabricItemSettings.() -> FabricItemSettings,
): I = constructor(block, settings(FabricItemSettings()))

fun <T : Item> createItem(
    constructor: (FabricItemSettings) -> T,
    settings: FabricItemSettings.() -> FabricItemSettings
): Item =
    constructor(settings(FabricItemSettings()))

val Item.isAir: Boolean
    get() = this == Items.AIR

val Item.nonAir: Item?
    get() = takeUnless { isAir }

fun Item.isInAny(vararg tags: Tag<Item>): Boolean = tags.any(::isIn)

fun Item.isInAll(vararg tags: Tag<Item>): Boolean = tags.all(::isIn)

//    ItemStack    //

@JvmField
val RECIPE_RESULT_CODEC: Codec<ItemStack> = RecordCodecBuilder.create { instance ->
    instance.group(
        Registry.ITEM.fieldOf("item").forGetter(ItemStack::getItem),
        Codec.INT.orElse(1).fieldOf("count").forGetter(ItemStack::getCount),
    ).apply(instance, ::ItemStack)
}

fun <T : Any> ItemStack.encode(dynamicOps: DynamicOps<T>, ignoreCount: Boolean): Result<T> =
    (if (ignoreCount) RECIPE_RESULT_CODEC else ItemStack.CODEC).encodeResult(dynamicOps, this)

fun <T : Any> decodeItemStack(dynamicOps: DynamicOps<T>, input: T, ignoreCount: Boolean): Result<ItemStack> =
    (if (ignoreCount) RECIPE_RESULT_CODEC else ItemStack.CODEC).decodeResult(dynamicOps, input)

fun ItemStack.isIn(tag: Tag<Item>): Boolean = item in tag

fun ItemStack.isInAny(vararg tags: Tag<Item>): Boolean = tags.any(::isIn)

fun ItemStack.isInAll(vararg tags: Tag<Item>): Boolean = tags.all(::isIn)