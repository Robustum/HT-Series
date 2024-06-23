package io.github.hiiragi283.api.material

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import io.github.hiiragi283.api.material.type.HTMaterialType
import io.github.hiiragi283.api.property.HTPropertyHolder

class HTMaterialRegistry(private val biMap: BiMap<HTMaterialKey, HTPropertyHolder>) {
    companion object {
        @JvmStatic
        fun build(builderAction: Builder.() -> Unit): HTMaterialRegistry {
            val map: MutableMap<HTMaterialKey, HTMaterialBuilder> = mutableMapOf()
            Builder(map).builderAction()
            return map
                .mapValues { it.value.build() }
                .let { HashBiMap.create(it) }
                .let(::HTMaterialRegistry)
        }
    }

    //    Map    //

    val keys: Set<HTMaterialKey>
        get() = biMap.keys

    val properties: Collection<HTPropertyHolder>
        get() = biMap.values

    operator fun contains(key: HTMaterialKey): Boolean = key in biMap

    operator fun get(key: HTMaterialKey): HTPropertyHolder? = biMap[key]

    fun getOrDefault(key: HTMaterialKey, defaultValue: HTPropertyHolder): HTPropertyHolder = get(key) ?: defaultValue

    fun getOrEmpty(key: HTMaterialKey): HTPropertyHolder = getOrDefault(key, HTPropertyHolder.EMPTY)

    fun getKey(material: HTPropertyHolder): HTMaterialKey? = biMap.inverse()[material]

    fun forEach(action: (HTMaterialKey, HTPropertyHolder) -> Unit) {
        biMap.forEach(action)
    }

    //    Builder    //

    class Builder(private val map: MutableMap<HTMaterialKey, HTMaterialBuilder>) {
        fun create(key: HTMaterialKey, type: HTMaterialType): HTMaterialBuilder {
            check(key !in map) { "Material builder; ${key.name} is already created!" }
            return map
                .computeIfAbsent(key) { HTMaterialBuilder(it, type) }
                .apply { key.validated = true }
        }

        fun createGas(key: HTMaterialKey) = create(key, HTMaterialType.Fluid.GAS)

        fun createGem(key: HTMaterialKey, gemType: HTMaterialType.Gem) = create(key, gemType)

        fun createLiquid(key: HTMaterialKey) = create(key, HTMaterialType.Fluid.LIQUID)

        fun createMetal(key: HTMaterialKey, isShiny: Boolean = true) = create(key, HTMaterialType.Metal.fromBoolean(isShiny))

        fun createSolid(key: HTMaterialKey) = create(key, HTMaterialType.Solid)

        fun createStone(key: HTMaterialKey) = create(key, HTMaterialType.Solid)

        fun createWood(key: HTMaterialKey) = create(key, HTMaterialType.Wood)

        fun getBuilder(key: HTMaterialKey): HTMaterialBuilder? = map[key]
    }
}
