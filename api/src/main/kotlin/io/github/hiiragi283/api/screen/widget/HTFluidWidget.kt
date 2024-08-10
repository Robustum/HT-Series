package io.github.hiiragi283.api.screen.widget

import io.github.cottonmc.cotton.gui.client.ScreenDrawing
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder
import io.github.cottonmc.cotton.gui.widget.WWidget
import io.github.hiiragi283.api.extension.sendC2SPacket
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.network.HTPacketCodec
import io.github.hiiragi283.api.storage.SlottedStorage
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.FluidState
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

@Suppress("UnstableApiUsage")
class HTFluidWidget(
    private val slottedStorage: SlottedStorage<FluidVariant, SingleSlotStorage<FluidVariant>>,
    private val index: Int,
    private val context: ScreenHandlerContext = ScreenHandlerContext.EMPTY,
) : WWidget() {
    private val singleStorage: SingleSlotStorage<FluidVariant>
        get() = slottedStorage.getSlot(index)

    private val variant: FluidVariant
        get() = singleStorage.resource

    private val fluid: Fluid
        get() = variant.fluid

    private val amount: Long
        get() = singleStorage.amount

    override fun paint(
        matrices: MatrixStack,
        x: Int,
        y: Int,
        mouseX: Int,
        mouseY: Int,
    ) {
        val handler: FluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluid) ?: return
        context.run { world, pos ->
            val state: FluidState = fluid.defaultState
            val spriteId: Identifier = handler.getFluidSprites(world, pos, state)[0].id
            val color: Int = handler.getFluidColor(world, pos, state)
            ScreenDrawing.texturedRect(matrices, x, y, width, height, spriteId, color)
        }
    }

    override fun addTooltip(tooltip: TooltipBuilder) {
        tooltip.add(FluidVariantRendering.getName(variant))
        tooltip.add(LiteralText("Amount - $amount"))
        FluidVariantRendering.getTooltip(variant).forEach(tooltip::add)
    }

    override fun onClick(x: Int, y: Int, button: Int) {
        super.onClick(x, y, button)
        context.run { world, pos ->
            val worldId: Identifier = world.registryManager
                .get(Registry.WORLD_KEY)
                .getId(world)
                ?: return@run
            sendC2SPacket(
                PACKET_CODEC,
                Context(
                    worldId,
                    pos,
                    variant,
                    amount,
                    index,
                ),
            )
        }
    }

    override fun canResize(): Boolean = false

    data class Context(
        val worldId: Identifier,
        val pos: BlockPos,
        val variant: FluidVariant,
        val amount: Long,
        val index: Int,
    )

    companion object {
        @JvmField
        val PACKET_CODEC: HTPacketCodec<Context> = HTPacketCodec.createSimple(
            HTModuleType.API.id("fluid_widget_sync"),
            { context, buf ->
                buf.writeIdentifier(context.worldId)
                buf.writeBlockPos(context.pos)
                context.variant.toPacket(buf)
                buf.writeVarLong(context.amount)
                buf.writeVarInt(context.index)
            },
            { buf ->
                val worldId: Identifier = buf.readIdentifier()
                val pos: BlockPos = buf.readBlockPos()
                val variant: FluidVariant = FluidVariant.fromPacket(buf)
                val amount: Long = buf.readVarLong()
                val index: Int = buf.readVarInt()
                Context(worldId, pos, variant, amount, index)
            },
        )
    }
}
