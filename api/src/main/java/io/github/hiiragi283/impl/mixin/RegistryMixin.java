package io.github.hiiragi283.impl.mixin;

import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Registry.class)
public class RegistryMixin {

    @Inject(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/registry/Registry;validate(Lnet/minecraft/util/registry/MutableRegistry;)V"))
    private static void ht_api$init(CallbackInfo ci) {
        Registry.ITEM.forEach(item -> {
            
        });
    }

}