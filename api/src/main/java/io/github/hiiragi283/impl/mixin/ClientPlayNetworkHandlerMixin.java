package io.github.hiiragi283.impl.mixin;

import io.github.hiiragi283.api.event.HTTagEvents;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.tag.TagManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow
    private TagManager tagManager;

    @Inject(method = "onSynchronizeTags", at = @At("TAIL"))
    private void ht_materials$onSynchronizeTags(SynchronizeTagsS2CPacket packet, CallbackInfo ci) {
        HTTagEvents.UPDATED.invoker().onUpdated(this.tagManager, true);
    }
}