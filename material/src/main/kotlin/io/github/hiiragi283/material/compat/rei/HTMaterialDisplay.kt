package io.github.hiiragi283.material.compat.rei

import io.github.hiiragi283.api.compat.rei.HTMaterialEntryStack
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.fluid.phase.HTPhasedMaterial
import io.github.hiiragi283.api.item.shape.HTShapedMaterial
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialTooltipContext
import io.github.hiiragi283.api.module.HTApiHolder
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.RecipeDisplay
import net.minecraft.util.Identifier
import java.util.function.Function

class HTMaterialDisplay(private val materialKey: HTMaterialKey) : RecipeDisplay {
    val entries: List<EntryStack> = buildList {
        // Fluid Entries
        HTFluidPhase.entries.forEach { phase ->
            val phasedMaterial: HTPhasedMaterial = HTPhasedMaterial(materialKey, phase)
            HTApiHolder.Material.apiInstance.materialFluidManager[phasedMaterial]
                .map { fluid ->
                    EntryStack.create(fluid).setting(
                        EntryStack.Settings.TOOLTIP_APPEND_EXTRA,
                        Function {
                            HTMaterialTooltipContext(materialKey, materialKey.get(), phase).createTooltips()
                        },
                    )
                }.forEach(::add)
        }
        // Item Entries
        HTApiHolder.Material.apiInstance.shapeRegistry.keys.forEach { key ->
            val shapedMaterial = HTShapedMaterial(materialKey, key)
            HTApiHolder.Material.apiInstance.materialItemManager[shapedMaterial]
                .map(EntryStack::create)
                .forEach(::add)
        }
    }
    val icon: EntryStack = HTMaterialEntryStack(materialKey)

    override fun getInputEntries(): List<List<EntryStack>> = listOf(entries)

    override fun getResultingEntries(): List<List<EntryStack>> = listOf(entries)

    override fun getRecipeCategory(): Identifier = HMREIPlugin.MATERIAL
}
