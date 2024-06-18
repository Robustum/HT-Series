package io.github.hiiragi283.material.compat.rei

import io.github.hiiragi283.api.compat.rei.HTMaterialEntryStack
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.fluid.phase.HTPhasedMaterial
import io.github.hiiragi283.api.item.shape.HTShapedMaterial
import io.github.hiiragi283.api.material.HTMaterial
import io.github.hiiragi283.api.module.HTApiHolder
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeDisplay
import net.minecraft.util.Identifier
import java.util.function.Function

class HTMaterialDisplay(val material: HTMaterial) : RecipeDisplay {
    val entries: List<EntryStack> = buildList {
        // Fluid Entries
        HTFluidPhase.entries.forEach { phase ->
            val phasedMaterial: HTPhasedMaterial.Direct = HTPhasedMaterial.direct(material, phase)
            HTApiHolder.Material.apiInstance.materialFluidManager[phasedMaterial]
                .map { fluid ->
                    EntryStack.create(fluid).setting(
                        EntryStack.Settings.TOOLTIP_APPEND_EXTRA,
                        Function {
                            HTMaterial.appendTooltip(material, phase)
                        },
                    )
                }.forEach(::add)
        }
        // Item Entries
        HTApiHolder.Material.apiInstance.shapeRegistry.values.forEach { shape ->
            val shapedMaterial: HTShapedMaterial.Direct = HTShapedMaterial.direct(material, shape)
            HTApiHolder.Material.apiInstance.materialItemManager[shapedMaterial]
                .map(EntryStack::create)
                .forEach(::add)
        }
    }
    val icon: EntryStack = HTMaterialEntryStack(material)

    override fun getInputEntries(): List<List<EntryStack>> = listOf(entries)

    override fun getResultingEntries(): List<List<EntryStack>> = listOf(entries)

    override fun getRecipeCategory(): Identifier = HMREIPlugin.MATERIAL
}
