package io.github.hiiragi283.api.extension.context

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextType
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos

data class HTLootDropContext(
    val serverWorld: ServerWorld,
    val pos: BlockPos,
    val player: PlayerEntity,
    val hand: Hand,
    val lootTable: LootTable,
) {
    val state: BlockState = serverWorld.getBlockState(pos)
    val blockEntity: BlockEntity? = serverWorld.getBlockEntity(pos)
    val stack: ItemStack = player.getStackInHand(hand)

    inline fun generateLoot(
        lootContextType: LootContextType,
        builderAction: (LootContext.Builder, HTLootDropContext) -> LootContext.Builder,
    ): List<ItemStack> = lootTable.generateLoot(
        builderAction(LootContext.Builder(serverWorld), this)
            .build(lootContextType),
    )
}
