package io.github.hiiragi283.api.material

import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.item.Item
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier

interface HTMaterialTagProvider {
    fun getTagId(keyable: HTMaterialKeyable): Identifier = getTagId(keyable.materialKey.name)

    fun getTagId(name: String): Identifier

    fun getItemTag(keyable: HTMaterialKeyable): Tag<Item> = getTag(keyable.materialKey.name, TagRegistry::item)

    fun getItemTag(name: String): Tag<Item> = getTag(name, TagRegistry::item)

    fun <T : Any> getTag(keyable: HTMaterialKeyable, tagBuilder: (Identifier) -> Tag<T>): Tag<T> =
        getTag(keyable.materialKey.name, tagBuilder)

    fun <T : Any> getTag(name: String, tagBuilder: (Identifier) -> Tag<T>): Tag<T> = tagBuilder(getTagId(name))
}
