package io.github.hiiragi283.api.item.shape

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.mojang.serialization.Codec

class HTShapeRegistry private constructor(private val biMap: BiMap<HTShapeKey, HTShape>) {
    companion object {
        @JvmStatic
        fun build(builderAction: Builder.() -> Unit): HTShapeRegistry {
            val map: MutableMap<HTShapeKey, HTShapeBuilder> = mutableMapOf()
            Builder(map).apply(builderAction)
            return map
                .mapValues { it.value.build() }
                .let { HashBiMap.create(it) }
                .let(::HTShapeRegistry)
        }

        @JvmStatic
        fun createCodec(shapeRegistry: HTShapeRegistry): Codec<HTShape> = HTShapeKey.CODEC.xmap(shapeRegistry::get, shapeRegistry::getKey)
    }

    //    Map    //

    val keys: Set<HTShapeKey>
        get() = biMap.keys

    val shapes: Collection<HTShape>
        get() = biMap.values

    operator fun contains(key: HTShapeKey): Boolean = key in biMap

    operator fun get(key: HTShapeKey): HTShape? = biMap[key]

    fun getOrDefault(key: HTShapeKey, defaultValue: HTShape): HTShape = get(key) ?: defaultValue

    fun getKey(shape: HTShape): HTShapeKey? = biMap.inverse()[shape]

    fun forEach(action: (HTShapeKey, HTShape) -> Unit) {
        biMap.forEach(action)
    }

    //    Builder    //

    class Builder(private val map: MutableMap<HTShapeKey, HTShapeBuilder>) {
        fun createBlockShape(key: HTShapeKey, idPath: String = "${key.name}_%s", tagPath: String = "${idPath}s"): HTShapeBuilder {
            check(key !in map) { "Shape builder; $key is already created!" }
            return map
                .computeIfAbsent(key) { HTShapeBuilder.createBlock(key, idPath, tagPath) }
                .apply { key.validated = true }
        }

        fun createItemShape(key: HTShapeKey, idPath: String = "${key.name}_%s", tagPath: String = "${idPath}s"): HTShapeBuilder {
            check(key !in map) { "Shape builder; $key is already created!" }
            return map
                .computeIfAbsent(key) { HTShapeBuilder.createItem(key, idPath, tagPath) }
                .apply { key.validated = true }
        }

        fun getBuilder(key: HTShapeKey): HTShapeBuilder? = map[key]

        fun isBlockShape(key: HTShapeKey): Boolean = getBuilder(key)?.allowBlockGen == true

        fun isItemShape(key: HTShapeKey): Boolean = getBuilder(key)?.allowItemGen == true
    }
}
