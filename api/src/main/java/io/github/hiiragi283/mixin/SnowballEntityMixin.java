package io.github.hiiragi283.mixin;

import io.github.hiiragi283.api.tag.HTEntityTypeTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowballEntity.class)
public abstract class SnowballEntityMixin extends ThrownItemEntity {

    public SnowballEntityMixin(EntityType<? extends SnowballEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onEntityHit", at = @At("HEAD"), cancellable = true)
    private void ht_api$onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        int damage = HTEntityTypeTags.SNOWBALL_CRITICAL.contains(entity.getType()) ? 3 : 0;
        entity.damage(DamageSource.thrownProjectile(this, getOwner()), damage);
        ci.cancel();
    }

}