package io.github.hiiragi283.api.recipe

import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.hiiragi283.api.extension.checkNotNull
import io.github.hiiragi283.api.extension.decodeResult
import io.github.hiiragi283.api.extension.tryGetValues
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.network.HTCodecSerializer
import io.github.hiiragi283.api.network.HTPacketCodec
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tag.ServerTagManagerHolder
import net.minecraft.tag.Tag
import net.minecraft.tag.TagGroup
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper
import net.minecraft.util.registry.Registry
import java.util.function.Consumer
import java.util.function.Predicate

interface HTIngredient : Predicate<ItemStack>, Consumer<ItemStack> {
    val previewStacks: List<ItemStack>

    companion object {
        @JvmStatic
        fun fromJson(jsonObject: JsonObject): HTIngredient {
            val type: HTCodecSerializer<*> = JsonHelper.getString(jsonObject, "type")
                .let(::Identifier)
                .let(HTCodecSerializer.REGISTRY::getOrEmpty)
                .orElseThrow { IllegalStateException("") }
            return type.codec.decodeResult(JsonOps.INSTANCE, jsonObject)
                .getOrNull()
                .let { it as? HTIngredient }
                .checkNotNull { "Could not read HTIngredient!" }
        }
    }

    //    Item    //

    data class ItemImpl(val item: Item, val count: Int) : HTIngredient {
        override val previewStacks: List<ItemStack> = listOf(ItemStack(item, count))

        override fun test(t: ItemStack): Boolean = !t.isEmpty && t.item == item && t.count >= count

        override fun accept(t: ItemStack) {
            t.decrement(count)
        }

        data object Serializer : HTCodecSerializer<ItemImpl> {
            override val codec: Codec<ItemImpl> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Registry.ITEM.fieldOf("item").forGetter(ItemImpl::item),
                    Codec.INT.orElse(1).fieldOf("count").forGetter(ItemImpl::count),
                ).apply(instance, ::ItemImpl)
            }

            override val packetCodec: HTPacketCodec<ItemImpl> = HTPacketCodec.createSimple(
                HTModuleType.API.id("ingredient/item"),
                { ingredient, buf ->
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

    //    Tag    //

    data class TagImpl(val tag: Tag<Item>, val count: Int) : HTIngredient {
        override val previewStacks: List<ItemStack> = tag.tryGetValues().map { ItemStack(it, count) }

        override fun test(t: ItemStack): Boolean = !t.isEmpty && t.item in tag && t.count >= count

        override fun accept(t: ItemStack) {
            t.decrement(count)
        }

        data object Serializer : HTCodecSerializer<TagImpl> {
            private val itemTags: TagGroup<Item>
                get() = ServerTagManagerHolder.getTagManager().items

            override val codec: Codec<TagImpl> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Tag.codec { itemTags }.fieldOf("tag").forGetter(TagImpl::tag),
                    Codec.INT.orElse(1).fieldOf("count").forGetter(TagImpl::count),
                ).apply(instance, ::TagImpl)
            }

            override val packetCodec: HTPacketCodec<TagImpl> = HTPacketCodec.createSimple(
                HTModuleType.API.id("ingredient/tag"),
                { ingredient, buf ->
                    buf.writeIdentifier(itemTags.getTagId(ingredient.tag))
                    buf.writeVarInt(ingredient.count)
                },
                { buf ->
                    val tag: Tag<Item> = itemTags.getTagOrEmpty(buf.readIdentifier())
                    val count: Int = buf.readVarInt()
                    TagImpl(tag, count)
                },
            )
        }
    }
}
