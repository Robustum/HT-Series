package io.github.hiiragi283.mixin;

import com.google.common.collect.ImmutableSet;
import io.github.hiiragi283.api.resource.MutableResourcePackManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ResourcePackManager.class)
public abstract class ResourcePackManagerMixin implements MutableResourcePackManager {

    @SuppressWarnings("ShadowModifiers")
    @Shadow
    private Set<ResourcePackProvider> providers;

    @Override
    public void ht_materials$addPackProvider(ResourcePackProvider provider) {
        providers = Util.make(ImmutableSet.<ResourcePackProvider>builder(), builder -> {
            builder.addAll(providers);
            builder.add(provider);
        }).build();
    }
}