package io.github.hiiragi283.api.tag

import io.github.hiiragi283.api.extension.commonId
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.entity.EntityType
import net.minecraft.tag.Tag

object HTEntityTypeTags {
    @JvmField
    val SNOWBALL_CRITICAL: Tag<EntityType<*>> = TagRegistry.entityType(commonId("critical/snowball"))
}
