package io.github.hiiragi283.api.item.shape

import io.github.hiiragi283.api.material.HTMaterialKeyable
import io.github.hiiragi283.api.material.HTMaterialTagProvider
import io.github.hiiragi283.api.module.HTModuleType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

data class HTShape internal constructor(
    val key: HTShapeKey,
    val idPath: String = "%s_${key.name}",
    val tagPath: String = "${idPath}s",
) : HTMaterialTagProvider {
    // fun <T : Any> typed(type: HTShapeType<T>) = Typed(simple, type)

    //    Identifier    //

    fun getId(keyable: HTMaterialKeyable, namespace: String = HTModuleType.MATERIAL.modId): Identifier =
        getId(keyable.materialKey.name, namespace)

    fun getId(name: String, namespace: String = HTModuleType.MATERIAL.modId): Identifier = Identifier(namespace, idPath.replace("%s", name))

    //    RegistryKey    //

    fun <T : Any> getRegistryKey(
        registryKey: RegistryKey<out Registry<T>>,
        keyable: HTMaterialKeyable,
        namespace: String = HTModuleType.MATERIAL.modId,
    ): RegistryKey<T> = getRegistryKey(registryKey, keyable.materialKey.name, namespace)

    fun <T : Any> getRegistryKey(
        registryKey: RegistryKey<out Registry<T>>,
        name: String,
        namespace: String = HTModuleType.MATERIAL.modId,
    ): RegistryKey<T> = RegistryKey.of(registryKey, getId(name, namespace))

    //    Tag    //

    override fun getTagId(name: String) = Identifier("c", tagPath.replace("%s", name))

    //    Typed    //

    /*data class Typed<T : Any>(override val simple: Simple, val type: HTShapeType<T>) : HTShape by simple {
        //    RegistryKey    //

        fun getRegistryKey(
            materialKey: HTMaterialKey,
            namespace: String = HTModuleType.MATERIAL.modId
        ): RegistryKey<T> =
            RegistryKey.of(type.registryKey, simple.getId(materialKey, namespace))

        fun getRegistryKey(name: String, namespace: String = HTModuleType.MATERIAL.modId): RegistryKey<T> =
            RegistryKey.of(type.registryKey, simple.getId(name, namespace))

        //    Tag    //

        fun getTag(materialKey: HTMaterialKey): Tag<T> = getTag(materialKey.name)

        fun getTag(name: String): Tag<T> = type.registryKey.createTag(simple.getTagId(name))
    }*/
}
