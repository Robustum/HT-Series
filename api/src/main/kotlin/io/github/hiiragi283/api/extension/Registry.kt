package io.github.hiiragi283.api.extension

import io.github.hiiragi283.mixin.RegistryKeyAccessor
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import kotlin.jvm.optionals.getOrNull

val <T : Any> RegistryKey<T>.registry: Identifier
    get() = (this as RegistryKeyAccessor).registry

fun <T : Any, U : T> Registry<T>.register(registryKey: RegistryKey<T>, value: U): U = Registry.register(this, registryKey.value, value)

fun <T : Any> Registry<T>.getKeyOrNull(entry: T): RegistryKey<T>? = getKey(entry).getOrNull()

@Suppress("UNCHECKED_CAST")
fun <T : Any> readRegistryKey(value: String): RegistryKey<T>? {
    val arranged: List<String> = value.removePrefix("ResourceKey[").removeSuffix("]").split("/")
    val registryId = Identifier(arranged[0])
    val registry: Registry<T> = Registry.REGISTRIES.get(registryId) as Registry<T>
    val valueId = Identifier(arranged[1])
    return RegistryKey.of(registry.key, valueId)
}

//    RegistryObject    //

fun <T : Any> Registry<T>.getRegistryObject(entry: T): RegistryObject<T>? = getKeyOrNull(entry)?.let { RegistryObject(entry, it.value) }

data class RegistryObject<T : Any>(val entry: T, val id: Identifier)