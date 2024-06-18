package io.github.hiiragi283.api.tag

import io.github.hiiragi283.api.module.HTModuleType
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.block.Block
import net.minecraft.tag.Tag

object HTBlockTags {
    @JvmField
    val REPLACED_WITH_PEAT: Tag<Block> = TagRegistry.block(HTModuleType.API.id("replaced_with_peat"))

    @JvmField
    val REPLACED_WITH_LIGNITE: Tag<Block> = TagRegistry.block(HTModuleType.API.id("replaced_with_lignite"))

    @JvmField
    val REPLACED_WITH_COAL: Tag<Block> = TagRegistry.block(HTModuleType.API.id("replaced_with_coal"))

    @JvmField
    val REPLACED_WITH_BITUMEN: Tag<Block> = TagRegistry.block(HTModuleType.API.id("replaced_with_bitumen"))
}
