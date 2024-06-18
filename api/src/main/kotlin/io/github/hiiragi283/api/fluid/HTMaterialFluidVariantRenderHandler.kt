package io.github.hiiragi283.api.fluid

import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.fluid.phase.HTPhasedMaterial
import io.github.hiiragi283.api.material.HTMaterial
import io.github.hiiragi283.api.material.HTMaterialKeyable
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

@Suppress("UnstableApiUsage")
class HTMaterialFluidVariantRenderHandler(phaseMaterial: HTPhasedMaterial) :
    FluidVariantRenderHandler,
    HTPhasedMaterial by phaseMaterial {
    constructor(keyable: HTMaterialKeyable, phase: HTFluidPhase) : this(HTPhasedMaterial.lazy(keyable, phase))

    override fun getName(fluidVariant: FluidVariant): Text = phase.getTranslatedText(materialKey)

    override fun appendTooltip(fluidVariant: FluidVariant, tooltip: MutableList<Text>, tooltipContext: TooltipContext) {
        HTMaterial.appendTooltip(material, phase, ItemStack.EMPTY, tooltip)
    }
}
