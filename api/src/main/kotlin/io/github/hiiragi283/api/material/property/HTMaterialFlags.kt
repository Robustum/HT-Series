package io.github.hiiragi283.api.material.property

import io.github.hiiragi283.api.extension.TypedIdentifier
import net.minecraft.util.Identifier

object HTMaterialFlags {
    private val registry: MutableMap<Identifier, TypedIdentifier<Unit>> = hashMapOf()

    @JvmStatic
    fun getOrCreateFlag(id: Identifier): TypedIdentifier<Unit> = registry.computeIfAbsent(id, TypedIdentifier.Companion::of)
}
