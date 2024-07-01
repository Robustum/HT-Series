package io.github.hiiragi283.mixin;

import io.github.hiiragi283.api.fluid.HTFluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.impl.client.rendering.fluid.FluidRenderHandlerRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.function.Function;

@Mixin(FluidRenderHandlerRegistryImpl.class)
public class FluidRenderHandlerRegistryImplMixin {

    @Shadow(remap = false)
    @Final
    private Map<Fluid, FluidRenderHandler> handlers;

    @Inject(method = "onFluidRendererReload", at = @At("RETURN"), remap = false)
    private void ht_materials$onFluidRendererReload(CallbackInfo ci) {
        Function<Identifier, Sprite> textureAtlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        for (FluidRenderHandler handler : handlers.values()) {
            if (handler instanceof HTFluidRenderHandler) {
                ((HTFluidRenderHandler) handler).reloadSprites(textureAtlas);
            }
        }
    }

}