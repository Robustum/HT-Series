package io.github.hiiragi283.api.item.shape

import io.github.hiiragi283.api.extension.checkNotNull
import io.github.hiiragi283.api.material.HTMaterialTranslatable
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTLogger
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

class HTShapeKey private constructor(val name: String) : Supplier<HTShape>, HTMaterialTranslatable {
    companion object {
        private val INSTANCES: MutableMap<String, HTShapeKey> = ConcurrentHashMap()

        @JvmStatic
        fun of(name: String): HTShapeKey = INSTANCES.computeIfAbsent(name, ::HTShapeKey)
    }

    //    Supplier    //

    var validated: Boolean = false
        internal set(value) {
            if (validated) return
            HTLogger.log { it.info("Shape; $name validated!") }
            field = value
        }

    fun checkValidation(): HTShapeKey = apply {
        check(validated) { "Shape; $name is not registered!" }
    }

    private var cache: HTShape? = null

    override fun get(): HTShape {
        checkValidation()
        if (cache == null) {
            cache = HTApiHolder.Material.apiInstance.shapeRegistry[this]
        }
        return cache.checkNotNull { "Shape; $name is not registered!" }
    }

    // fun <T : Any> getTyped(type: HTShapeType<T>): HTShape.Typed<T> = get().typed(type)

    //    HTMaterialTranslatable    //

    override val translationKey: String = "ht_shape.$name"

    //    Any    //

    override fun equals(other: Any?): Boolean = (other as? HTShapeKey)?.name == this.name

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = name
}
