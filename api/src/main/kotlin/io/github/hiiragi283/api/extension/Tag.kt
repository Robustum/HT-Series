package io.github.hiiragi283.api.extension

import io.github.hiiragi283.impl.mixin.TagBuilderAccessor
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier

fun <T : Any> Tag<T>.tryGetValues(): List<T> = runCatchAndLog { values() }
    .getOrDefault(emptyList())

private val Tag.Builder.entries: MutableList<Tag.TrackedEntry>
    get() = (this as TagBuilderAccessor).entries

fun Tag.Builder.remove(id: Identifier): Tag.Builder = apply {
    entries.removeIf { entry ->
        entry.entry.toString().removeSuffix("?").let {
            !it.startsWith("#") && it == id.toString()
        }
    }
}

fun Tag.Builder.removeTag(id: Identifier): Tag.Builder = apply {
    entries.removeIf { entry ->
        entry.entry.toString().removeSuffix("?").let {
            it.startsWith("#") && it.removePrefix("#") == id.toString()
        }
    }
}
