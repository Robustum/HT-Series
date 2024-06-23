package io.github.hiiragi283.api.property

import io.github.hiiragi283.api.extension.TypedIdentifier

interface HTPropertyHolder {
    operator fun <T : Any> get(id: TypedIdentifier<T>): T?

    fun <T : Any> getOrDefault(id: TypedIdentifier<T>, defaultValue: T): T = get(id) ?: defaultValue

    operator fun contains(id: TypedIdentifier<*>): Boolean

    fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit)

    interface Mutable : HTPropertyHolder {
        operator fun <T : Any> set(id: TypedIdentifier<T>, value: T)

        fun remove(id: TypedIdentifier<*>)
    }

    companion object {
        @JvmField
        val EMPTY: HTPropertyHolder = Empty

        @JvmStatic
        fun create(map: MutableMap<TypedIdentifier<*>, Any> = mutableMapOf(), builderAction: Mutable.() -> Unit = {}): HTPropertyHolder =
            Builder(map).apply(builderAction)
    }

    private object Empty : HTPropertyHolder {
        override fun <T : Any> get(id: TypedIdentifier<T>): T? = null

        override fun contains(id: TypedIdentifier<*>): Boolean = false

        override fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit) = Unit
    }

    private class Builder(private val map: MutableMap<TypedIdentifier<*>, Any>) : Mutable {
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
