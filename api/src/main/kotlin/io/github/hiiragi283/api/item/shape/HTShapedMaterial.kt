package io.github.hiiragi283.api.item.shape

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.hiiragi283.api.extension.runCatchAndLog
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.property.HTPropertyHolder

data class HTShapedMaterial(val materialKey: HTMaterialKey, val shapeKey: HTShapeKey) {
    val material: HTPropertyHolder
        get() = materialKey.get()
    val shape: HTShape
        get() = shapeKey.get()

    val materialOrNull: HTPropertyHolder?
        get() = runCatchAndLog { material }.getOrNull()

    val shapeOrNull: HTShape?
        get() = runCatchAndLog { shape }.getOrNull()

    fun checkValidation() {
        materialKey.checkValidation()
        shapeKey.checkValidation()
    }

    operator fun component3(): HTPropertyHolder = material

    operator fun component4() = shape

    companion object {
        @JvmField
        val CODEC: Codec<HTShapedMaterial> = RecordCodecBuilder.create { instance ->
            instance.group(
                HTMaterialKey.CODEC.fieldOf("materialKey").forGetter(HTShapedMaterial::materialKey),
                HTShapeKey.CODEC.fieldOf("shapeKey").forGetter(HTShapedMaterial::shapeKey),
            ).apply(instance, ::HTShapedMaterial)
        }

        @JvmField
        val COMPARATOR: Comparator<HTShapedMaterial> =
            compareBy<HTShapedMaterial> { it.shapeKey.name }.thenBy { it.materialKey.name }
    }
}
