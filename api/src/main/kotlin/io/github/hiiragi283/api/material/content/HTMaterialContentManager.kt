package io.github.hiiragi283.api.material.content

import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.shape.HTShapeKey
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.Fluids
import net.minecraft.item.Item
import net.minecraft.item.Items

class HTMaterialContentManager(
    val blockGroup: HTMaterialContentGroup<HTShapeKey, Block>,
    val fluidGroup: HTMaterialContentGroup<HTFluidPhase, Fluid>,
    val itemGroup: HTMaterialContentGroup<HTShapeKey, Item>,
) {
    companion object {
        @JvmField
        val EMPTY = HTMaterialContentManager(
            HTMaterialContentGroup.buildEmpty(Blocks.AIR),
            HTMaterialContentGroup.buildEmpty(Fluids.EMPTY),
            HTMaterialContentGroup.buildEmpty(Items.AIR),
        )
    }
}
