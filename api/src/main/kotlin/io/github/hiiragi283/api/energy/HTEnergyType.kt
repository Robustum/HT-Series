package io.github.hiiragi283.api.energy

import io.github.hiiragi283.api.extension.lowerName
import io.github.hiiragi283.api.module.HTModuleType
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier

enum class HTEnergyType {
    COOLANT,
    ELECTRICITY,
    ENCHANTMENT,
    HEAT,
    SOUL,
    ;

    fun getTagId(level: HTEnergyLevel): Identifier = HTModuleType.ENGINEERING.id("$lowerName/${level.lowerName}")

    fun getBlockTag(level: HTEnergyLevel): Tag<Block> = TagRegistry.block(getTagId(level))

    fun getItemTag(level: HTEnergyLevel): Tag<Item> = TagRegistry.item(getTagId(level))
}
