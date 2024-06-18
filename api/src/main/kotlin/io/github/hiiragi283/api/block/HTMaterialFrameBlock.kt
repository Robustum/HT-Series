package io.github.hiiragi283.api.block

import io.github.hiiragi283.api.extension.disableSpawning
import io.github.hiiragi283.api.extension.nonSolid
import io.github.hiiragi283.api.extension.nonSuffocates
import io.github.hiiragi283.api.extension.transparentVision
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.text.MutableText

class HTMaterialFrameBlock : Block(
    FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
        .nonOpaque()
        .disableSpawning()
        .nonSolid()
        .nonSuffocates()
        .transparentVision(),
) {
    override fun getName(): MutableText = asItem().name as MutableText
}
