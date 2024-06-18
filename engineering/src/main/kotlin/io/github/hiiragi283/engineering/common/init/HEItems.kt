package io.github.hiiragi283.engineering.common.init

import io.github.hiiragi283.api.extension.createBlockItem
import io.github.hiiragi283.api.item.HTItemSettings
import io.github.hiiragi283.api.module.HTModuleType
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.registry.Registry

object HEItems {
    @JvmField
    val BRICK_WRAPPED_TERRACOTTA: BlockItem = registerBlockItem(
        "brick_wrapped_terracotta",
        HEBlocks.BRICK_WRAPPED_TERRACOTTA,
    ) { group(ItemGroup.BUILDING_BLOCKS) }

    @JvmField
    val STEEL_PLATED_STONE: BlockItem = registerBlockItem(
        "steel_plated_stone",
        HEBlocks.STEEL_PLATED_STONE,
    ) { group(ItemGroup.BUILDING_BLOCKS) }

    @JvmField
    val STEEL_WRAPPED_STONE: BlockItem = registerBlockItem(
        "steel_wrapped_stone",
        HEBlocks.STEEL_WRAPPED_STONE,
    ) { group(ItemGroup.BUILDING_BLOCKS) }

    @JvmField
    val DREADY_WRAPPED_STONE: BlockItem = registerBlockItem(
        "dready_wrapped_stone",
        HEBlocks.DREADY_WRAPPED_STONE,
    ) { group(ItemGroup.BUILDING_BLOCKS) }

    @JvmField
    val STEADY_WRAPPED_STONE: BlockItem = registerBlockItem(
        "steady_wrapped_stone",
        HEBlocks.STEADY_WRAPPED_STONE,
    ) { group(ItemGroup.BUILDING_BLOCKS) }

    @JvmField
    val CAULDRON: BlockItem = registerBlockItem(
        "cauldron",
        HEBlocks.CAULDRON,
    ) { group(ItemGroup.DECORATIONS) }

    @JvmField
    val PRIMITIVE_BLAST_FURNACE: BlockItem = registerBlockItem(
        "primitive_blast_furnace",
        HEBlocks.PRIMITIVE_BLAST_FURNACE,
    ) { group(ItemGroup.DECORATIONS) }

    private fun <T : Item> registerItem(path: String, item: T): T = Registry.register(
        Registry.ITEM,
        HTModuleType.ENGINEERING.id(path),
        item,
    )

    private fun registerBlockItem(path: String, block: Block, settings: HTItemSettings.() -> HTItemSettings): BlockItem =
        registerItem(path, createBlockItem(block, settings))
}
