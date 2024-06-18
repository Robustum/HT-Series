package io.github.hiiragi283.engineering.common.init

import io.github.hiiragi283.api.block.HTMultiblockControllerBlock
import io.github.hiiragi283.api.block.HTSimpleBlockWithEntity
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.engineering.common.block.entity.HTBlastingFurnaceBlockEntity
import io.github.hiiragi283.engineering.common.block.entity.HTCauldronBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.util.registry.Registry

object HEBlocks {
    @JvmField
    val BRICK_WRAPPED_TERRACOTTA: Block = registerBlock(
        "brick_wrapped_terracotta",
        Block(FabricBlockSettings.copy(Blocks.TERRACOTTA)),
    )

    @JvmField
    val STEEL_PLATED_STONE: Block = registerBlock(
        "steel_plated_stone",
        Block(FabricBlockSettings.copy(Blocks.SMOOTH_STONE)),
    )

    @JvmField
    val STEEL_WRAPPED_STONE: Block = registerBlock(
        "steel_wrapped_stone",
        Block(FabricBlockSettings.copy(Blocks.SMOOTH_STONE)),
    )

    @JvmField
    val DREADY_WRAPPED_STONE: Block = registerBlock(
        "dready_wrapped_stone",
        Block(FabricBlockSettings.copy(Blocks.NETHER_BRICKS)),
    )

    @JvmField
    val STEADY_WRAPPED_STONE: Block = registerBlock(
        "steady_wrapped_stone",
        Block(FabricBlockSettings.copy(Blocks.POLISHED_BLACKSTONE)),
    )

    @JvmField
    val CAULDRON: Block = registerBlock(
        "cauldron",
        HTSimpleBlockWithEntity(
            FabricBlockSettings.copy(Blocks.CAULDRON),
            ::HTCauldronBlockEntity,
        ),
    )

    @JvmField
    val PRIMITIVE_BLAST_FURNACE = registerBlock(
        "primitive_blast_furnace",
        HTMultiblockControllerBlock(
            FabricBlockSettings.copy(Blocks.BRICKS),
            ::HTBlastingFurnaceBlockEntity,
        ),
    )

    private fun <T : Block> registerBlock(path: String, block: T): T = Registry.register(
        Registry.BLOCK,
        HTModuleType.ENGINEERING.id(path),
        block,
    )
}
