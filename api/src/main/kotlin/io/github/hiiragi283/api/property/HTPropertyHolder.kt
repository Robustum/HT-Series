package io.github.hiiragi283.api.property

import io.github.hiiragi283.api.extension.TypedIdentifier

interface HTPropertyHolder {
    operator fun <T : Any> get(id: TypedIdentifier<T>): T?

    fun <T : Any> getOrDefault(id: TypedIdentifier<T>, defaultValue: T): T = get(id) ?: defaultValue

    operator fun contains(id: TypedIdentifier<*>): Boolean

    fun <T : Any> ifPresent(id: TypedIdentifier<T>, action: (T) -> Unit) {
        get(id)?.let(action)
    }

    fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit)

    interface Mutable : HTPropertyHolder {
        operator fun <T : Any> set(id: TypedIdentifier<T>, value: T)

        fun <T : Any> setIfNonNull(id: TypedIdentifier<T>, value: T?) {
            value?.let { set(id, it) }
        }

        fun remove(id: TypedIdentifier<*>)

        fun <T : Any> removeIf(id: TypedIdentifier<T>, filter: (T) -> Boolean) {
            val existValue: T = get(id) ?: return
            if (filter(existValue)) {
                remove(id)
            }
        }

        fun <T : Any> removeIfNull(id: TypedIdentifier<T>, mapping: (T) -> Any?) {
            val existValue: T = get(id) ?: return
            if (mapping(existValue) == null) {
                remove(id)
            }
        }

        fun <T : Any> computeIfAbsent(id: TypedIdentifier<T>, mapping: () -> T): T {
            val value: T? = get(id)
            if (value == null) {
                val newValue: T = mapping()
                set(id, newValue)
                return newValue
            } else {
                return value
            }
        }
    }

    companion object {
        @JvmField
        val EMPTY: HTPropertyHolder = Empty

        @JvmStatic
        fun create(map: MutableMap<TypedIdentifier<*>, Any> = mutableMapOf(), builderAction: Mutable.() -> Unit = {}): HTPropertyHolder =
            create(map, ::Impl, builderAction)

        @JvmStatic
        fun <T : Any> create(
            map: MutableMap<TypedIdentifier<*>, Any> = mutableMapOf(),
            build: (HTPropertyHolder) -> T,
            builderAction: Mutable.() -> Unit = {},
        ): T = Builder(map).apply(builderAction).let(build)

        @JvmStatic
        fun builder(map: MutableMap<TypedIdentifier<*>, Any> = mutableMapOf()): Mutable = Builder(map)
    }

    private object Empty : HTPropertyHolder {
        override fun <T : Any> get(id: TypedIdentifier<T>): T? = null

        override fun contains(id: TypedIdentifier<*>): Boolean = false

        override fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit) = Unit
    }

    private class Impl(delegate: HTPropertyHolder) : HTPropertyHolder by delegate

    open class Builder(private val map: MutableMap<TypedIdentifier<*>, Any>) : Mutable {
        override fun <T : Any> get(id: TypedIdentifier<T>): T? = id.cast(map[id])

        override fun contains(id: TypedIdentifier<*>): Boolean = id in map

        override fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit) {
            map.forEach(action)
        }

        override fun <T : Any> set(id: TypedIdentifier<T>, value: T) {
            map[id] = value
        }

        override fun remove(id: TypedIdentifier<*>) {
            map.remove(id)
        }
    }
}
