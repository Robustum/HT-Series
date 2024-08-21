package io.github.hiiragi283.api.material.property

import io.github.hiiragi283.api.resource.HTModelJsonBuilder
import net.minecraft.block.Block
import net.minecraft.client.color.block.BlockColorProvider
import net.minecraft.data.client.model.BlockStateSupplier
import net.minecraft.item.Item
import net.minecraft.loot.LootTable

interface HTMaterialBlockModelHandler : BlockColorProvider {
    fun buildLootTable(block: Block): LootTable.Builder

    fun buildBlockModel(modelBuilder: HTModelJsonBuilder, block: Block)

    fun buildBlockState(block: Block): BlockStateSupplier

    fun buildItemBlockModel(modelBuilder: HTModelJsonBuilder, item: Item)
}
