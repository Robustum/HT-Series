package io.github.hiiragi283.api.block

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.mixin.`object`.builder.AbstractBlockAccessor
import net.minecraft.block.AbstractBlock
import net.minecraft.block.MapColor
import net.minecraft.block.Material

class HTBlockSettings : FabricBlockSettings {
    constructor(settings: AbstractBlock.Settings) : super(settings)

    constructor(block: AbstractBlock) : super((block as AbstractBlockAccessor).settings)

    constructor(material: Material, color: MapColor) : super(material, color)
}
