package io.github.hiiragi283.engineering.common.init

import io.github.hiiragi283.api.block.entity.HTMultiblockControllerBlockEntity
import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.machine.HTMachineType
import io.github.hiiragi283.api.machine.multiblock.HTBlockPredicate
import io.github.hiiragi283.api.machine.multiblock.HTMultiblockShape
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.storage.HTStorageSide
import io.github.hiiragi283.engineering.common.block.entity.HTBlastingFurnaceBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

object HEMachineTypes {
    @JvmField
    val CAULDRON: HTMachineType.Single = registerSingle(
        "cauldron",
        HEBlocks.CAULDRON,
    ) { type ->
        when (type) {
            HTEnergyType.COOLANT -> HTStorageSide.DOWN
            HTEnergyType.ELECTRICITY -> HTStorageSide.NONE
            HTEnergyType.ENCHANTMENT -> HTStorageSide.NONE
            HTEnergyType.HEAT -> HTStorageSide.DOWN
            HTEnergyType.SOUL -> HTStorageSide.DOWN
        }
    }

    @JvmField
    val PRIMITIVE_BLAST_FURNACE: HTMachineType.Multi<HTBlastingFurnaceBlockEntity> = registerMulti(
        "primitive_blast_furnace",
        HEBlocks.PRIMITIVE_BLAST_FURNACE,
        ::HTBlastingFurnaceBlockEntity,
        HTMultiblockShape.Builder.create {
            addHollow(-1..1, 0, -1..1, HTBlockPredicate.of(HEBlocks.BRICK_WRAPPED_TERRACOTTA))
                .addHollow(-1..1, 1, -1..1, HTBlockPredicate.of(Blocks.BRICKS))
                .addHollow(-1..1, 2, -1..1, HTBlockPredicate.of(Blocks.BRICKS))
                .addPillar(1, 1..2, 1, HTBlockPredicate.of(Blocks.BRICK_WALL))
                .addPillar(1, 1..2, -1, HTBlockPredicate.of(Blocks.BRICK_WALL))
                .addPillar(-1, 1..2, 1, HTBlockPredicate.of(Blocks.BRICK_WALL))
                .addPillar(-1, 1..2, -1, HTBlockPredicate.of(Blocks.BRICK_WALL))
                .remove(0, 0, -1)
        },
    ) { type ->
        when (type) {
            HTEnergyType.HEAT -> HTStorageSide.SIDE
            else -> HTStorageSide.NONE
        }
    }

    private fun registerSingle(path: String, block: Block, energySides: (HTEnergyType) -> HTStorageSide): HTMachineType.Single =
        register(path, HTMachineType.Single(block, energySides))

    private fun <T : HTMultiblockControllerBlockEntity> registerMulti(
        path: String,
        block: Block,
        supplier: () -> T,
        multiblockShape: HTMultiblockShape,
        energySides: (HTEnergyType) -> HTStorageSide,
    ): HTMachineType.Multi<T> = register(path, HTMachineType.Multi(block, supplier, multiblockShape, energySides))

    private fun <T : HTMachineType<*>> register(path: String, machineType: T): T {
        val id: Identifier = HTModuleType.API.id(path)
        Registry.register(HTMachineType.REGISTRY, id, machineType)
        Registry.register(Registry.BLOCK_ENTITY_TYPE, id, machineType)
        Registry.register(Registry.RECIPE_SERIALIZER, id, machineType)
        Registry.register(Registry.RECIPE_TYPE, id, machineType)
        return machineType
    }
}
