package io.github.hiiragi283.api.machine.multiblock

import io.github.hiiragi283.api.block.entity.HTMultiblockControllerBlockEntity
import io.github.hiiragi283.api.extension.getOrNull
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction

@Environment(EnvType.CLIENT)
class HTMultiblockRenderer<T : HTMultiblockControllerBlockEntity>(renderDispatcher: BlockEntityRenderDispatcher) :
    BlockEntityRenderer<T>(renderDispatcher) {
    override fun render(
        entity: T,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int,
    ) {
        if (!entity.showPreview) return
        val direction: Direction? = entity.cachedState.getOrNull(Properties.HORIZONTAL_FACING)
        entity.world?.let { entity.machineType.multiblockShape.render(matrices, vertexConsumers, it, direction) }
    }
}
