package io.github.hiiragi283.impl.mixin;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegistryKey.class)
public interface RegistryKeyAccessor {
    
    @Accessor
    Identifier getRegistry();
    
}