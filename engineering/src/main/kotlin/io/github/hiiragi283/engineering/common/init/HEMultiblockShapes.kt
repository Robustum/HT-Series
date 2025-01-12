package io.github.hiiragi283.engineering.common.init

import io.github.hiiragi283.api.machine.multiblock.HTBlockPredicate
import io.github.hiiragi283.api.machine.multiblock.HTMultiblockShape
import io.github.hiiragi283.api.module.HTModuleType
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.util.registry.Registry

object HEMultiblockShapes {
    @JvmField
    val BLAST_FURNACE: HTMultiblockShape = registerLargeFurnace(
        "blast_furnace",
        HEBlocks.STEEL_WRAPPED_STONE,
        HEBlocks.STEEL_PLATED_STONE,
        Blocks.STONE_BRICK_WALL,
    )

    @JvmField
    val NETHER_FURNACE: HTMultiblockShape = registerLargeFurnace(
        "nether_furnace",
        Blocks.CHISELED_NETHER_BRICKS,
        Blocks.WARPED_STEM,
        Blocks.NETHER_BRICK_WALL,
    )

    @JvmField
    val BLACK_FURNACE: HTMultiblockShape = registerLargeFurnace(
        "black_furnace",
        Blocks.CHISELED_POLISHED_BLACKSTONE,
        Blocks.POLISHED_BASALT,
        Blocks.BLACKSTONE_WALL,
    )

    private fun registerLargeFurnace(
        path: String,
        base: Block,
        edge: Block,
        pillar: Block,
    ): HTMultiblockShape = register(path) {
        addLayer(-1..1, 0, -1..1, HTBlockPredicate.of(base))
            .addHollow(-1..1, 1, -1..1, HTBlockPredicate.of(edge))
            .addHollow(-1..1, 2, -1..1, HTBlockPredicate.of(edge))
            .addPillar(1, 1..3, 1, HTBlockPredicate.of(pillar))
            .addPillar(1, 1..3, -1, HTBlockPredicate.of(pillar))
            .addPillar(-1, 1..3, 1, HTBlockPredicate.of(pillar))
            .addPillar(-1, 1..3, -1, HTBlockPredicate.of(pillar))
    }

    private fun register(path: String, builderAction: HTMultiblockShape.Builder.() -> Unit): HTMultiblockShape = Registry.register(
        HTMultiblockShape.REGISTRY,
        HTModuleType.ENGINEERING.id(path),
        HTMultiblockShape.Builder.create(builderAction),
    )
}
