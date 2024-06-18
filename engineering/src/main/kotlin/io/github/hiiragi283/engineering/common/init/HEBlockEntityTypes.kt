package io.github.hiiragi283.engineering.common.init

import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.engineering.common.block.entity.HTBlastingFurnaceBlockEntity
import io.github.hiiragi283.engineering.common.block.entity.HTCauldronBlockEntity
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.registry.Registry

object HEBlockEntityTypes {
    @JvmField
    val CAULDRON: BlockEntityType<HTCauldronBlockEntity> =
        registerBlockEntityType("cauldron", ::HTCauldronBlockEntity, HEBlocks.CAULDRON)

    @JvmField
    val PRIMITIVE_BLAST_FURNACE: BlockEntityType<HTBlastingFurnaceBlockEntity> =
        registerBlockEntityType("blasting_furnace", ::HTBlastingFurnaceBlockEntity, HEBlocks.PRIMITIVE_BLAST_FURNACE)

    private fun <T : BlockEntity> registerBlockEntityType(path: String, builder: () -> T, vararg blocks: Block): BlockEntityType<T> =
        Registry.register(
            Registry.BLOCK_ENTITY_TYPE,
            HTModuleType.ENGINEERING.id(path),
            BlockEntityType.Builder.create(builder, *blocks).build(null),
        )
}
