package io.github.hiiragi283.api.fluid.phase

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import io.github.hiiragi283.api.extension.RegistryObject
import io.github.hiiragi283.api.extension.getRegistryObject
import io.github.hiiragi283.api.extension.nonAir
import io.github.hiiragi283.api.extension.safeValues
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialKeyable
import io.github.hiiragi283.api.module.HTModuleType
import net.minecraft.fluid.Fluid
import net.minecraft.tag.Tag
import net.minecraft.util.registry.Registry

class HTMaterialFluidManager(
    private val fluidToPhased: Map<Fluid, HTPhasedMaterial.Lazy>,
    private val phasedToFluids: Table<HTMaterialKey, HTFluidPhase, MutableSet<Fluid>>,
) {
    constructor() : this(mapOf(), HashBasedTable.create())

    //    Fluid -> HTPhasedMaterial    //

    operator fun get(fluid: Fluid): HTPhasedMaterial? = fluidToPhased[fluid]

    operator fun contains(fluid: Fluid): Boolean = fluid in fluidToPhased

    //    HTPhasedMaterial -> Fluid    //

    fun convert(fluid: Fluid): Fluid? = get(fluid)?.let(::getOrNull)

    fun getOrNull(phasedMaterial: HTPhasedMaterial): Fluid? = getOrNull(phasedMaterial.materialKey, phasedMaterial.phase)

    fun getOrNull(materialKey: HTMaterialKey, phase: HTFluidPhase): Fluid? {
        val fluids: List<RegistryObject<Fluid>> = phasedToFluids[materialKey, phase]
            ?.mapNotNull(Registry.FLUID::getRegistryObject)
            ?: return null
        val first: RegistryObject<Fluid> = fluids.firstOrNull { it.id.namespace == "minecraft" }
            ?: fluids.firstOrNull { it.id.namespace == HTModuleType.MATERIAL.modId }
            ?: fluids.getOrNull(0)
            ?: return null
        return first.entry
    }

    //    HTPhasedMaterial -> Set<Fluid>    //

    operator fun get(phasedMaterial: HTPhasedMaterial): Set<Fluid> = get(phasedMaterial.materialKey, phasedMaterial.phase)

    operator fun get(materialKey: HTMaterialKey, phase: HTFluidPhase): Set<Fluid> = phasedToFluids.get(materialKey, phase) ?: setOf()

    operator fun contains(phasedMaterial: HTPhasedMaterial): Boolean = contains(phasedMaterial.materialKey, phasedMaterial.phase)

    fun contains(materialKey: HTMaterialKey, phase: HTFluidPhase): Boolean = phasedToFluids.contains(materialKey, phase)

    //    Builder    //

    class Builder(private val map: MutableMap<Entry, HTPhasedMaterial.Lazy>) {
        @JvmOverloads
        fun add(
            keyable: HTMaterialKeyable,
            phase: HTFluidPhase,
            fluid: Fluid?,
            unification: Boolean = true,
        ) {
            add(HTPhasedMaterial.lazy(keyable, phase), fluid, unification)
        }

        private fun add(part: HTPhasedMaterial.Lazy, fluid: Fluid?, unification: Boolean = true) {
            part.checkValidation()
            fluid?.nonAir?.run {
                map[Entry.FluidEntry(this, unification)] = part
            }
        }

        @JvmOverloads
        fun add(
            materialKey: HTMaterialKey,
            phase: HTFluidPhase,
            tag: Tag<Fluid>,
            unification: Boolean = true,
        ) {
            add(HTPhasedMaterial.lazy(materialKey, phase), tag, unification)
        }

        private fun add(part: HTPhasedMaterial.Lazy, tag: Tag<Fluid>, unification: Boolean = true) {
            part.checkValidation()
            map[Entry.TagEntry(tag, unification)] = part
        }
    }

    sealed interface Entry {
        val unification: Boolean
        val values: List<Fluid>

        data class FluidEntry(val fluid: Fluid, override val unification: Boolean) : Entry {
            override val values: List<Fluid> = listOf(fluid)
        }

        data class TagEntry(val tag: Tag<Fluid>, override val unification: Boolean) : Entry {
            override val values: List<Fluid>
                get() = tag.safeValues()
        }
    }
}
