package io.github.hiiragi283.api.extension

import io.github.hiiragi283.api.extension.context.HTLootDropContext
import io.github.hiiragi283.api.extension.context.HTSoundContext
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.context.LootContextType
import net.minecraft.server.world.ServerWorld
import net.minecraft.tag.Tag
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Predicate

inline fun <reified T : Entity> ServerWorld.getAllEntitiesByType(type: EntityType<T>, filter: Predicate<Entity>): List<T> =
    getEntitiesByType(type, filter).filterIsInstance<T>()

fun dropStackAt(player: PlayerEntity, stack: ItemStack) {
    dropStackAt(player.world, player.blockPos, stack)
}

fun dropStackAt(world: World, pos: BlockPos, stack: ItemStack) {
    val itemEntity = ItemEntity(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
    itemEntity.setPickupDelay(0)
    world.spawnEntity(itemEntity)
}

inline fun dropLootAt(
    world: World,
    pos: BlockPos,
    player: PlayerEntity,
    hand: Hand,
    lootTableId: Identifier,
    tag: Tag<Item>,
    lootContextType: LootContextType,
    soundContext: HTSoundContext? = null,
    builderAction: (LootContext.Builder, HTLootDropContext) -> LootContext.Builder,
): ActionResult = dropLootAt(world, pos, player, hand, lootTableId, { item.isIn(tag) }, lootContextType, soundContext, builderAction)

inline fun dropLootAt(
    world: World,
    pos: BlockPos,
    player: PlayerEntity,
    hand: Hand,
    lootTableId: Identifier,
    stackPredicate: ItemStack.() -> Boolean,
    lootContextType: LootContextType,
    soundContext: HTSoundContext? = null,
    builderAction: (LootContext.Builder, HTLootDropContext) -> LootContext.Builder,
): ActionResult = player.getStackInHand(hand).let { stack: ItemStack ->
    if (stackPredicate(stack)) {
        if (!world.isClient && world is ServerWorld) {
            world.server.lootManager.getTable(lootTableId)?.let { lootTable: LootTable ->
                HTLootDropContext(
                    world,
                    pos,
                    player,
                    hand,
                    lootTable,
                )
                    .generateLoot(lootContextType, builderAction)
                    .forEach { dropStackAt(player, it) }
            }
            stack.decrement(1)
            soundContext?.playSound(world, pos)
        }
        ActionResult.success(world.isClient)
    }
    ActionResult.PASS
}
