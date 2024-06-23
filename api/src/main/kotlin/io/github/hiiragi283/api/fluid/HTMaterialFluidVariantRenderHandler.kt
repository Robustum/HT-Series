package io.github.hiiragi283.api.fluid

import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialTooltipContext
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.client.item.TooltipContext
import net.minecraft.text.Text

@Suppress("UnstableApiUsage")
class HTMaterialFluidVariantRenderHandler(val materialKey: HTMaterialKey, val phase: HTFluidPhase) :
    FluidVariantRenderHandler {
    override fun getName(fluidVariant: FluidVariant): Text = phase.getTranslatedText(materialKey)

    override fun appendTooltip(fluidVariant: FluidVariant, tooltip: MutableList<Text>, tooltipContext: TooltipContext) {
        HTMaterialTooltipContext(materialKey, materialKey.get(), phase).appendTooltips(tooltip)
    }
}
