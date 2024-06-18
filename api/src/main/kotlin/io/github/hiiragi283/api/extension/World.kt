package io.github.hiiragi283.api.extension

import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
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
