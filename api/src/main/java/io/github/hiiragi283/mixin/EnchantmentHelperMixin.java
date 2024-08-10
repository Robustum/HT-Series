package io.github.hiiragi283.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.hiiragi283.api.event.HTItemEvent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Redirect(method = {
            "calculateRequiredExperienceLevel",
            "generateEnchantments"
    }, at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getEnchantability()I"))
    private static int ht_api$getEnchantability(Item instance, @Local(argsOnly = true) ItemStack stack) {
        Integer enchantability = HTItemEvent.ENCHANTABILITY.invoker().getEnchantability(stack);
        return enchantability == null ? stack.getItem().getEnchantability() : enchantability;
    }

}