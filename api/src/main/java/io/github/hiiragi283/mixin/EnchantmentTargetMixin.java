package io.github.hiiragi283.mixin;

import io.github.hiiragi283.impl.HTMixinImpls;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
        "net/minecraft/enchantment/EnchantmentTarget$1",
        "net/minecraft/enchantment/EnchantmentTarget$2",
        "net/minecraft/enchantment/EnchantmentTarget$3",
        "net/minecraft/enchantment/EnchantmentTarget$4",
        "net/minecraft/enchantment/EnchantmentTarget$5",
        "net/minecraft/enchantment/EnchantmentTarget$6",
        "net/minecraft/enchantment/EnchantmentTarget$7",
})
public abstract class EnchantmentTargetMixin {

    @Inject(method = "isAcceptableItem", at = @At("RETURN"), cancellable = true)
    private void ht_api$isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(HTMixinImpls.isAcceptableItem((EnchantmentTarget) (Object) this, item));
    }

}