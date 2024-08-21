package io.github.hiiragi283.api.material

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.hiiragi283.api.extension.runCatchAndLog
import io.github.hiiragi283.api.property.HTPropertyHolder

data class HTTypedMaterialKey<T : Comparable<T>>(
    val materialKey: HTMaterialKey,
    val type: T,
) : Comparable<HTTypedMaterialKey<T>> {
    val material: HTPropertyHolder
        get() = materialKey.get()

    val materialOrNull: HTPropertyHolder?
        get() = runCatchAndLog { material }.getOrNull()

    val typeClass: Class<T>
        get() = type.javaClass

    fun checkValidation(typeValidation: (T) -> Unit) {
        materialKey.checkValidation()
        typeValidation(type)
    }

    //    Comparable    //

    override fun compareTo(other: HTTypedMaterialKey<T>): Int = when (val keyOrder: Int = materialKey.compareTo(other.materialKey)) {
        0 -> type.compareTo(other.type)
        else -> keyOrder
    }

    companion object {
        @JvmStatic
        fun <T : Comparable<T>> createCodec(typeCodec: Codec<T>): Codec<HTTypedMaterialKey<T>> = RecordCodecBuilder.create { instance ->
            instance.group(
                HTMaterialKey.CODEC.fieldOf("material_key").forGetter(HTTypedMaterialKey<T>::materialKey),
                typeCodec.fieldOf("type").forGetter(HTTypedMaterialKey<T>::type),
            ).apply(instance, ::HTTypedMaterialKey)
        }
    }
}
