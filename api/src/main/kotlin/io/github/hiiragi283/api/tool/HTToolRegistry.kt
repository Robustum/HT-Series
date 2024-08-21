package io.github.hiiragi283.api.tool

import com.google.common.collect.BiMap
import com.google.common.collect.ImmutableBiMap
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTTypedMaterialKey
import io.github.hiiragi283.api.property.HTPropertyHolder

class HTToolRegistry(private val biMap: BiMap<HTTypedMaterialKey<HTToolClass>, out HTTool>) {
    companion object {
        @JvmStatic
        fun build(builderAction: Builder.() -> Unit): HTToolRegistry {
            val map: MutableMap<HTTypedMaterialKey<HTToolClass>, HTPropertyHolder.Mutable> = mutableMapOf()
            Builder(map).apply(builderAction)
            val builder: ImmutableBiMap.Builder<HTTypedMaterialKey<HTToolClass>, HTTool> = ImmutableBiMap.builder()
            map.forEach { (key: HTTypedMaterialKey<HTToolClass>, value: HTPropertyHolder.Mutable) ->
                val toolClass: HTToolClass = key.type
                builder.put(key, toolClass.toolFunction(HTToolProperty(value)))
            }
            return HTToolRegistry(builder.build())
        }
    }

    //    Map    //

    val keys: Set<HTTypedMaterialKey<HTToolClass>>
        get() = biMap.keys

    val properties: Collection<HTTool>
        get() = biMap.values

    operator fun contains(key: HTTypedMaterialKey<HTToolClass>): Boolean = key in biMap

    operator fun get(key: HTTypedMaterialKey<HTToolClass>): HTTool? = biMap[key]

    fun getOrDefault(key: HTTypedMaterialKey<HTToolClass>, defaultValue: HTTool): HTTool = get(key) ?: defaultValue

    fun getKey(material: HTTool): HTTypedMaterialKey<HTToolClass>? = biMap.inverse()[material]

    fun forEach(action: (HTTypedMaterialKey<HTToolClass>, HTTool) -> Unit) {
        biMap.forEach(action)
    }

    //    Builder    //

    class Builder(private val map: MutableMap<HTTypedMaterialKey<HTToolClass>, HTPropertyHolder.Mutable>) {
        fun getOrCreate(toolClass: HTToolClass, materialKey: HTMaterialKey) {
            map.computeIfAbsent(HTTypedMaterialKey(materialKey, toolClass)) { HTPropertyHolder.builder() }
        }
    }
}
