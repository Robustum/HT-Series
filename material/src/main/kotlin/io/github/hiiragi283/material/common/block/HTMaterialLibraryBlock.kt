package io.github.hiiragi283.material.common.block

import io.github.hiiragi283.api.block.HTItemConvertingBlock
import io.github.hiiragi283.api.module.HTApiHolder
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

object HTMaterialLibraryBlock : HTItemConvertingBlock(
    FabricBlockSettings.copyOf(Blocks.BOOKSHELF).requiresTool().breakByTool(FabricToolTags.AXES),
) {
    override fun convertStack(world: WorldAccess, pos: BlockPos, stack: ItemStack): List<ItemStack> = 
        HTApiHolder.Material
            .apiInstance
            .materialItemManager
            .convert(stack)
            .let(::listOf)
}