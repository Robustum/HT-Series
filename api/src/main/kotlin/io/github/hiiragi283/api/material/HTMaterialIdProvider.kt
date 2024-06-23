package io.github.hiiragi283.api.material

import io.github.hiiragi283.api.module.HTModuleType
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

interface HTMaterialIdProvider {
    fun getId(materialKey: HTMaterialKey, namespace: String = HTModuleType.MATERIAL.modId): Identifier = getId(materialKey.name, namespace)

    fun getId(name: String, namespace: String = HTModuleType.MATERIAL.modId): Identifier

    fun <T : Any> getRegistryKey(
        registryKey: RegistryKey<out Registry<T>>,
        materialKey: HTMaterialKey,
        namespace: String = HTModuleType.MATERIAL.modId,
    ): RegistryKey<T> = getRegistryKey(registryKey, materialKey.name, namespace)

    fun <T : Any> getRegistryKey(
        registryKey: RegistryKey<out Registry<T>>,
        name: String,
        namespace: String = HTModuleType.MATERIAL.modId,
    ): RegistryKey<T> = RegistryKey.of(registryKey, getId(name, namespace))
}
