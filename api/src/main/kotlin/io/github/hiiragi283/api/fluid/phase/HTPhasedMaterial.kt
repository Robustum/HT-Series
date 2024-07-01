package io.github.hiiragi283.api.fluid.phase

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.github.hiiragi283.api.extension.runCatchAndLog
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.property.HTPropertyHolder

data class HTPhasedMaterial(
    val materialKey: HTMaterialKey,
    val phase: HTFluidPhase,
) {
    val material: HTPropertyHolder
        get() = materialKey.get()

    val materialOrNull: HTPropertyHolder?
        get() = runCatchAndLog { material }.getOrNull()

    fun checkValidation() {
        materialKey.checkValidation()
    }

    operator fun component3(): HTPropertyHolder = material

    companion object {
        @JvmField
        val CODEC: Codec<HTPhasedMaterial> = RecordCodecBuilder.create { instance ->
            instance.group(
                HTMaterialKey.CODEC.fieldOf("materialKey").forGetter(HTPhasedMaterial::materialKey),
                HTFluidPhase.CODEC.fieldOf("phase").forGetter(HTPhasedMaterial::phase),
            ).apply(instance, ::HTPhasedMaterial)
        }

        @JvmField
        val COMPARATOR: Comparator<HTPhasedMaterial> = compareBy(HTPhasedMaterial::phase).thenBy { it.materialKey.name }
    }
}
