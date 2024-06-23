package io.github.hiiragi283.api.material

import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.item.Item
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier

interface HTMaterialTagProvider {
    fun getTagId(materialKey: HTMaterialKey): Identifier = getTagId(materialKey.name)

    fun getTagId(name: String): Identifier

    fun getItemTag(materialKey: HTMaterialKey): Tag<Item> = getTag(materialKey.name, TagRegistry::item)

    fun getItemTag(name: String): Tag<Item> = getTag(name, TagRegistry::item)

    fun <T : Any> getTag(materialKey: HTMaterialKey, tagBuilder: (Identifier) -> Tag<T>): Tag<T> = getTag(materialKey.name, tagBuilder)

    fun <T : Any> getTag(name: String, tagBuilder: (Identifier) -> Tag<T>): Tag<T> = tagBuilder(getTagId(name))
}
