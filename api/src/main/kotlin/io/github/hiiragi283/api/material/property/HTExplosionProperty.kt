package io.github.hiiragi283.api.material.property

import net.minecraft.entity.ItemEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.damage.DamageSource
import net.minecraft.world.World
import net.minecraft.world.explosion.Explosion

enum class HTExplosionProperty(val power: Float) {
    LOW(2.0f),
    MIDDLE(4.0f),
    HIGH(6.0f),
    ;

    fun createExplosion(world: World, entity: ItemEntity) {
        world.createExplosion(
            entity,
            DamageSource.explosion(null as LivingEntity?),
            null,
            entity.x,
            entity.y,
            entity.z,
            power,
            true,
            Explosion.DestructionType.DESTROY,
        )
    }
}
