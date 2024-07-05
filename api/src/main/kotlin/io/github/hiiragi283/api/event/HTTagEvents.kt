package io.github.hiiragi283.api.event

import io.github.hiiragi283.api.event.HTTagEvents.Build
import io.github.hiiragi283.api.event.HTTagEvents.Updated
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialTagProvider
import io.github.hiiragi283.api.module.HTModuleType
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.tag.Tag
import net.minecraft.tag.TagManager
import net.minecraft.util.Identifier

object HTTagEvents {
    //    Build    //

    private fun <T : Any> createBuildEvent(): Event<Build<T>> = EventFactory.createArrayBacked(Build::class.java) { listeners ->
        Build { handler -> listeners.forEach { it.register(handler) } }
    }

    @JvmField
    val BLOCK: Event<Build<Block>> = createBuildEvent()

    @JvmField
    val ITEM: Event<Build<Item>> = createBuildEvent()

    @JvmField
    val FLUID: Event<Build<Fluid>> = createBuildEvent()

    @JvmField
    val ENTITY_TYPE: Event<Build<EntityType<*>>> = createBuildEvent()

    fun interface Build<T : Any> {
        fun register(handler: Handler<T>)

        class Handler<T>(private val map: MutableMap<Identifier, Tag.Builder>, private val mapper: (T) -> Identifier) {
            fun getOrCreate(materialKey: HTMaterialKey, tagProvider: HTMaterialTagProvider): Tag.Builder =
                getOrCreate(tagProvider.getTagId(materialKey))

            fun getOrCreate(id: Identifier): Tag.Builder = map.computeIfAbsent(id) { Tag.Builder.create() }

            fun getOrCreate(tag: Tag<T>): Tag.Builder? = (tag as? Tag.Identified<T>)?.id?.let(::getOrCreate)

            fun add(materialKey: HTMaterialKey, tagProvider: HTMaterialTagProvider, vararg obj: T): Handler<T> =
                add(tagProvider.getTagId(materialKey), *obj)

            fun add(id: Identifier, vararg obj: T): Handler<T> = apply {
                val builder: Tag.Builder = getOrCreate(id)
                obj.forEach { builder.add(mapper(it), HTModuleType.API.modName) }
            }

            fun add(tag: Tag<T>, vararg obj: T): Handler<T> = apply {
                val builder: Tag.Builder = getOrCreate(tag) ?: return@apply
                obj.forEach { builder.add(mapper(it), HTModuleType.API.modName) }
            }
        }
    }

    //    Updated    //

    @JvmField
    val UPDATED: Event<Updated> = EventFactory.createArrayBacked(Updated::class.java) { listeners ->
        Updated { tagManager, isClient -> listeners.forEach { it.onUpdated(tagManager, isClient) } }
    }

    fun interface Updated {
        fun onUpdated(tagManager: TagManager, isClient: Boolean)
    }
}
