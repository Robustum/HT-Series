package io.github.hiiragi283.api.extension

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler
import net.minecraft.client.texture.Sprite
import net.minecraft.util.Identifier
import java.util.function.Function

interface HTFluidRenderHandler : FluidRenderHandler {
    fun reloadSprites(textureAtlas: Function<Identifier, Sprite>)
}
