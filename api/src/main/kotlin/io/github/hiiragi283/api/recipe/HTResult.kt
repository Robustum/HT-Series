package io.github.hiiragi283.api.recipe

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.hiiragi283.api.extension.checkNotNull
import io.github.hiiragi283.api.extension.decodeResult
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.network.HTCodecSerializer
import io.github.hiiragi283.api.network.HTPacketCodec
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import java.util.function.Predicate
import java.util.function.UnaryOperator

interface HTResult : Predicate<ItemStack>, UnaryOperator<ItemStack> {
    val previewStacks: List<ItemStack>

    companion object {
        @JvmStatic
        fun fromJson(jsonObject: JsonObject): HTResult {
            val type: HTCodecSerializer<*> = JsonHelper.getString(jsonObject, "type")
                .let(::Identifier)
                .let(HTCodecSerializer.REGISTRY::getOrEmpty)
                .orElseThrow { IllegalStateException("") }
            return type.codec.decodeResult(JsonOps.INSTANCE, jsonObject)
                .getOrNull()
                .let { it as? HTResult }
                .checkNotNull { "Could not read HTResult!" }
        }
    }

    //    Item    //

    data class ItemImpl(val item: Item, val count: Int) : HTResult {
        override val previewStacks: List<ItemStack> = listOf(ItemStack(item, count))

        override fun test(t: ItemStack): Boolean = t.isEmpty || t.item == item

        override fun apply(t: ItemStack): ItemStack = if (t.isEmpty) previewStacks[0] else t.apply { increment(count) }

        data object Serializer : HTCodecSerializer<ItemImpl> {
            override val codec: Codec<ItemImpl> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Registry.ITEM.fieldOf("item").forGetter(ItemImpl::item),
                    Codec.INT.orElse(1).fieldOf("count").forGetter(ItemImpl::count),
                ).apply(instance, ::ItemImpl)
            }

            override val packetCodec: HTPacketCodec<ItemImpl> = HTPacketCodec.createSimple(
                HTModuleType.API.id("result/item"),
                { buf, ingredient ->
                    buf.writeVarInt(Registry.ITEM.getRawId(ingredient.item))
                    buf.writeVarInt(ingredient.count)
                },
                { buf ->
                    val item: Item = Registry.ITEM.get(buf.readVarInt())
                    val count: Int = buf.readVarInt()
                    ItemImpl(item, count)
                },
            )
        }
    }

    //    Enchantment (test)    //

    data class EnchantImpl(val enchantmentLevelEntry: EnchantmentLevelEntry) : HTResult {
        constructor(enchantment: Enchantment, level: Int) : this(EnchantmentLevelEntry(enchantment, level))

        val enchantment: Enchantment
            get() = enchantmentLevelEntry.enchantment

        val level: Int
            get() = enchantmentLevelEntry.level

        override val previewStacks: List<ItemStack> = listOf(EnchantedBookItem.forEnchantment(enchantmentLevelEntry))

        override fun test(t: ItemStack): Boolean = EnchantmentHelper.get(t).isEmpty()

        override fun apply(t: ItemStack): ItemStack = t.apply {
            EnchantedBookItem.addEnchantment(this, enchantmentLevelEntry)
        }

        data object Serializer : HTCodecSerializer<EnchantImpl> {
            override val codec: Codec<EnchantImpl> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Registry.ENCHANTMENT.fieldOf("enchantment").forGetter(EnchantImpl::enchantment),
                    Codec.INT.orElse(1).fieldOf("level").forGetter(EnchantImpl::level),
                ).apply(instance, ::EnchantImpl)
            }

            override val packetCodec: HTPacketCodec<EnchantImpl> = HTPacketCodec.createSimple(
                HTModuleType.API.id("result/enchantment"),
                { buf, ingredient ->
                    buf.writeVarInt(Registry.ENCHANTMENT.getRawId(ingredient.enchantment))
                    buf.writeVarInt(ingredient.level)
                },
                { buf ->
                    val enchantment: Enchantment = Registry.ENCHANTMENT.get(buf.readVarInt())
                        .checkNotNull { "Invalid enchantment found!" }
                    val level: Int = buf.readVarInt()
                    EnchantImpl(enchantment, level)
                },
            )
        }
    }
}
