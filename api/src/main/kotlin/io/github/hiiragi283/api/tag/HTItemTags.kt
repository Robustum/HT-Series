package io.github.hiiragi283.api.tag

import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.item.Item
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier

object HTItemTags {
    @JvmField
    val ARMOR: Tag<Item> = TagRegistry.item(Identifier("fabric", "armor"))

    @JvmField
    val ARMOR_FEET: Tag<Item> = TagRegistry.item(Identifier("fabric", "armor/feet"))

    @JvmField
    val ARMOR_LEGS: Tag<Item> = TagRegistry.item(Identifier("fabric", "armor/legs"))

    @JvmField
    val ARMOR_CHEST: Tag<Item> = TagRegistry.item(Identifier("fabric", "armor/chest"))

    @JvmField
    val ARMOR_HEAD: Tag<Item> = TagRegistry.item(Identifier("fabric", "armor/head"))
}
