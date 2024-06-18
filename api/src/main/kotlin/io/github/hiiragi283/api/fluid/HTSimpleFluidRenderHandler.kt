package io.github.hiiragi283.api.fluid

import net.minecraft.client.texture.Sprite
import net.minecraft.fluid.FluidState
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockRenderView
import java.util.function.Function

class HTSimpleFluidRenderHandler(
    private val stillTexture: Identifier,
    private val flowingTexture: Identifier,
    private val color: Int = -1,
) : HTFluidRenderHandler {
    private val sprites: Array<Sprite?> = arrayOfNulls(2)

    override fun reloadSprites(textureAtlas: Function<Identifier, Sprite>) {
        sprites[0] = textureAtlas.apply(stillTexture)
        sprites[1] = textureAtlas.apply(flowingTexture)
    }

    override fun getFluidSprites(view: BlockRenderView?, pos: BlockPos?, state: FluidState): Array<Sprite?> = sprites

    override fun getFluidColor(view: BlockRenderView?, pos: BlockPos?, state: FluidState): Int = color
}
