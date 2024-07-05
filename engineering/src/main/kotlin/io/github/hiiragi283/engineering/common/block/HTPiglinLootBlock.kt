package io.github.hiiragi283.engineering.common.block

import io.github.hiiragi283.api.block.HTItemConvertingBlock
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootTables
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldAccess

object HTPiglinLootBlock : HTItemConvertingBlock(FabricBlockSettings.copyOf(Blocks.BLACKSTONE)) {
    override fun convertStack(world: WorldAccess, pos: BlockPos, stack: ItemStack): List<ItemStack> =
        if (!world.isClient && world is ServerWorld) {
            val server: MinecraftServer = world.server
            server.lootManager.getTable(LootTables.PIGLIN_BARTERING_GAMEPLAY)
                ?.generateLoot(
                    LootContext.Builder(world)
                        // .parameter(LootContextParameters.THIS_ENTITY, null)
                        .random(world.random)
                        .build(LootContextTypes.EMPTY),
                )
                ?: listOf()
        } else {
            listOf()
        }
}
