package io.github.hiiragi283.api.item.shape

import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.property.HTPropertyHolder

data class HTShapedMaterial(val materialKey: HTMaterialKey, val shapeKey: HTShapeKey) {
    val material: HTPropertyHolder
        get() = materialKey.get()
    val shape: HTShape
        get() = shapeKey.get()

    val materialOrNull: HTPropertyHolder?
        get() = runCatching { material }.getOrNull()

    val shapeOrNull: HTShape?
        get() = runCatching { shape }.getOrNull()

    fun checkValidation() {
        materialKey.checkValidation()
        shapeKey.checkValidation()
    }

    operator fun component3(): HTPropertyHolder = material

    operator fun component4() = shape

    companion object {
        @JvmField
        val COMPARATOR: Comparator<HTShapedMaterial> =
            compareBy<HTShapedMaterial> { it.shapeKey.name }.thenBy { it.materialKey.name }
    }
}
