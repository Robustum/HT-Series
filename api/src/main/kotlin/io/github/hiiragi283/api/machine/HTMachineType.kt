package io.github.hiiragi283.api.machine

import io.github.hiiragi283.api.block.entity.HTEnergySourceFinder
import io.github.hiiragi283.api.block.entity.HTMachineBlockEntity
import io.github.hiiragi283.api.block.entity.HTMultiblockControllerBlockEntity
import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.machine.multiblock.HTMultiblockShape
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.recipe.HTRecipeType
import io.github.hiiragi283.api.storage.HTStorageSide
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.SimpleRegistry

sealed class HTMachineType<T : HTMachineBlockEntity>(val block: Block) :
    BlockEntityType<T>(null, setOf(block), null), HTRecipeType, HTEnergySourceFinder {
    companion object {
        @JvmField
        val REGISTRY: SimpleRegistry<HTMachineType<*>> =
            FabricRegistryBuilder.createSimple(HTMachineType::class.java, HTModuleType.API.id("machine_type"))
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister()
    }

    //    HTRecipeType    //

    override val name: String by lazy { toString().split(":")[1] }

    override fun icon(): ItemStack = block.asItem().defaultStack

    //    HTEnergySourceFinder    //

    override fun pos(): BlockPos {
        throw AssertionError()
    }

    //    Any    //

    override fun toString(): String = REGISTRY.getKey(this).orElseThrow().value.toString()

    //    Single    //

    class Single(
        block: Block,
        val energySides: (HTEnergyType) -> HTStorageSide,
    ) : HTMachineType<HTMachineBlockEntity>(block) {
        override fun instantiate(): HTMachineBlockEntity = HTMachineBlockEntity(this)

        override fun getValidSide(type: HTEnergyType): HTStorageSide = energySides(type)
    }

    //    Multiblock    //

    class Multi<T : HTMultiblockControllerBlockEntity>(
        block: Block,
        val supplier: () -> T,
        val multiblockShape: HTMultiblockShape,
        val energySides: (HTEnergyType) -> HTStorageSide,
    ) : HTMachineType<T>(block) {
        override fun instantiate(): T = supplier()

        override fun getValidSide(type: HTEnergyType): HTStorageSide = energySides(type)
    }
}
