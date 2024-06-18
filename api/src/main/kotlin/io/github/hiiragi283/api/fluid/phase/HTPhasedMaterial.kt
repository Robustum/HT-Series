package io.github.hiiragi283.api.fluid.phase

import io.github.hiiragi283.api.material.HTMaterial
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialKeyable

interface HTPhasedMaterial {
    val material: HTMaterial
    val phase: HTFluidPhase

    val materialKey: HTMaterialKey

    val materialOrNull: HTMaterial?
        get() = runCatching { materialOrNull }.getOrNull()

    fun checkValidation() {
        materialKey.checkValidation()
    }

    companion object {
        @JvmField
        val COMPARATOR: Comparator<HTPhasedMaterial> = compareBy(HTPhasedMaterial::phase).thenBy { it.materialKey.name }

        @JvmStatic
        fun direct(material: HTMaterial, phase: HTFluidPhase) = Direct(material, phase)

        @JvmStatic
        fun lazy(other: HTPhasedMaterial) = Lazy(other.materialKey, other.phase)

        @JvmStatic
        fun lazy(keyable: HTMaterialKeyable, phase: HTFluidPhase) = Lazy(keyable.materialKey, phase)
    }

    data class Direct internal constructor(
        override val material: HTMaterial,
        override val phase: HTFluidPhase,
    ) : HTPhasedMaterial {
        override val materialKey: HTMaterialKey = material.materialKey
    }

    data class Lazy internal constructor(
        override val materialKey: HTMaterialKey,
        override val phase: HTFluidPhase,
    ) : HTPhasedMaterial {
        override val material: HTMaterial by lazy { materialKey.get() }
    }
}
