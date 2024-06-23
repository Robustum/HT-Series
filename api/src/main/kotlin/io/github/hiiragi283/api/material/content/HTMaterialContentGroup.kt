package io.github.hiiragi283.api.material.content

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import io.github.hiiragi283.api.material.HTMaterialKey
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

class HTMaterialContentGroup<T : Any, U : Any> private constructor(
    private val table: Table<HTMaterialKey, T, U>,
    private val defaultValue: U,
) {
    companion object {
        @JvmStatic
        fun <T : Any, U : Any> buildEmpty(defaultValue: U): HTMaterialContentGroup<T, U> =
            HTMaterialContentGroup(HashBasedTable.create(), defaultValue)

        @JvmStatic
        fun <T : Any, U : Any> buildAndRegister(
            defaultValue: U,
            registry: Registry<U>,
            idFunction: (HTMaterialKey, T) -> Identifier,
            builderAction: Builder<T, U>.() -> Unit,
        ): HTMaterialContentGroup<T, U> {
            val table = HashBasedTable.create<HTMaterialKey, T, U>()
            Builder(table).builderAction()
            return HTMaterialContentGroup(table, defaultValue).apply {
                forEach { key, type, obj ->
                    Registry.register(registry, idFunction(key, type), obj)
                }
            }
        }
    }

    fun contains(materialKey: HTMaterialKey, type: T): Boolean = table.contains(materialKey, type)

    fun get(materialKey: HTMaterialKey, type: T): U? = table.get(materialKey, type)

    fun getResult(materialKey: HTMaterialKey, type: T): Result<U> = runCatching { get(materialKey, type)!! }

    fun getOrDefault(materialKey: HTMaterialKey, type: T): U = get(materialKey, type) ?: defaultValue

    fun forEach(action: (HTMaterialKey, T, U) -> Unit) {
        table.cellSet().forEach cell@{ cell ->
            val key: HTMaterialKey = cell.rowKey ?: return@cell
            val type: T = cell.columnKey ?: return@cell
            val obj: U = cell.value ?: return@cell
            action(key, type, obj)
        }
    }

    class Builder<T : Any, U : Any> internal constructor(private val table: Table<HTMaterialKey, T, U>) {
        fun add(materialKey: HTMaterialKey, type: T, obj: U) = apply {
            table.put(materialKey, type, obj)
        }

        fun remove(materialKey: HTMaterialKey, type: T) = apply {
            table.remove(materialKey, type)
        }
    }
}
