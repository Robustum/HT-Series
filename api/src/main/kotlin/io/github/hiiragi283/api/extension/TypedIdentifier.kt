package io.github.hiiragi283.api.extension

import net.minecraft.util.Identifier

data class TypedIdentifier<T : Any>(val clazz: Class<T>, val id: Identifier) : Identifiable {
    companion object {
        @JvmStatic
        inline fun <reified T : Any> of(id: Identifier) = TypedIdentifier(T::class.java, id)

        @JvmStatic
        inline fun <reified T : Any> of(namespace: String, path: String) = TypedIdentifier(T::class.java, Identifier(namespace, path))
    }

    override fun asIdentifier(): Identifier = id

    @Suppress("UNCHECKED_CAST")
    fun cast(obj: Any?): T? = obj as? T
}
