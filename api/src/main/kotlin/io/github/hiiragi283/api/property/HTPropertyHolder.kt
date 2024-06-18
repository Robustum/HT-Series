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
    }

    private object Empty : HTPropertyHolder {
        override fun <T : Any> get(id: TypedIdentifier<T>): T? = null

        override fun contains(id: TypedIdentifier<*>): Boolean = false

        override fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit) = Unit
    }
}
