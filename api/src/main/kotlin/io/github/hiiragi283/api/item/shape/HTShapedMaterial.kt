package io.github.hiiragi283.api.item.shape

import io.github.hiiragi283.api.material.HTMaterial
import io.github.hiiragi283.api.material.HTMaterialKey

interface HTShapedMaterial {
    val material: HTMaterial
    val shape: HTShape

    val materialKey: HTMaterialKey

    val materialOrNull: HTMaterial?
        get() = runCatching { material }.getOrNull()

    val shapeKey: HTShapeKey

    val shapeOrNull: HTShape?
        get() = runCatching { shape }.getOrNull()

    fun checkValidation() {
        materialKey.checkValidation()
        shapeKey.checkValidation()
    }

    companion object {
        @JvmField
        val COMPARATOR: Comparator<HTShapedMaterial> =
            compareBy<HTShapedMaterial> { it.shapeKey.name }.thenBy { it.materialKey.name }

        @JvmStatic
        fun direct(material: HTMaterial, shape: HTShape) = Direct(material, shape)

        @JvmStatic
        fun lazy(shapedMaterial: HTShapedMaterial) = Lazy(shapedMaterial.materialKey, shapedMaterial.shapeKey)

        @JvmStatic
        fun lazy(materialKey: HTMaterialKey, shapeKey: HTShapeKey) = Lazy(materialKey, shapeKey)

        /*@JvmStatic
        fun <T : Any> typed(shapedMaterial: HTShapedMaterial, type: HTShapeType<T>) =
            typed(shapedMaterial.materialKey, shapedMaterial.shapeKey, type)

        @JvmStatic
        fun <T : Any> typed(materialKey: HTMaterialKey, keyConvertible: HTShapeKeyConvertible, type: HTShapeType<T>) =
            Typed(materialKey, keyConvertible.shapeKey, type)*/
    }

    data class Direct internal constructor(
        override val material: HTMaterial,
        override val shape: HTShape,
    ) : HTShapedMaterial {
        override val materialKey: HTMaterialKey = material.materialKey
        override val shapeKey: HTShapeKey = shape.key
    }

    data class Lazy internal constructor(
        override val materialKey: HTMaterialKey,
        override val shapeKey: HTShapeKey,
    ) : HTShapedMaterial {
        override val material: HTMaterial by lazy { materialKey.get() }
        override val shape: HTShape by lazy { shapeKey.get() }
    }

    /*data class Typed<T : Any> internal constructor(
        override val materialKey: HTMaterialKey,
        override val shapeKey: HTShapeKey,
        val shapeType: HTShapeType<T>,
    ) : HTShapedMaterial {
        override val material: HTMaterial by lazy { materialKey.get() }
        override val shape: HTShape.Simple by lazy { shapeKey.get() }

        val typedShape: HTShape.Typed<T> = HTShape.Typed(shape, shapeType)

        val rootKey: RegistryKey<Registry<T>> = shapeType.registryKey

        val registryKey = typedShape.getRegistryKey(materialKey)

        val tag by lazy { typedShape.getTag(materialKey) }
    }*/
}
