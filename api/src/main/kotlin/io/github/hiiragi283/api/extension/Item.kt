package io.github.hiiragi283.api.extension

import com.mojang.serialization.Codec
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.hiiragi283.api.item.HTItemSettings
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.registry.Registry

//    Item    //

fun createItem(settings: HTItemSettings.() -> HTItemSettings): Item = createItem(::Item, settings)

fun createBlockItem(block: Block, settings: HTItemSettings.() -> HTItemSettings): BlockItem = createBlockItem(block, ::BlockItem, settings)

fun <B : Block, I : Item> createBlockItem(
    block: B,
    constructor: (B, HTItemSettings) -> I,
    settings: HTItemSettings.() -> HTItemSettings,
): I = constructor(block, settings(HTItemSettings()))

fun <T : Item> createItem(constructor: (HTItemSettings) -> T, settings: HTItemSettings.() -> HTItemSettings): Item =
    constructor(settings(HTItemSettings()))

val Item.isAir: Boolean
    get() = this == Items.AIR

val Item.nonAir: Item?
    get() = takeUnless { isAir }

val Item.propertyHolder: HTPropertyHolder
    get() = this as HTPropertyHolder

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
