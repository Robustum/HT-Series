package io.github.hiiragi283.mixin;

import io.github.hiiragi283.api.event.HTPlayerEvents;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Unique
    private PlayerEntity self() {
        return (PlayerEntity) (Object) this;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void ht_api$startTick(CallbackInfo ci) {
        HTPlayerEvents.START_TICK.invoker().onStartTick(self());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void ht_api$endTick(CallbackInfo ci) {
        HTPlayerEvents.END_TICK.invoker().onEndTick(self());
    }

}