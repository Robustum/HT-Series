package io.github.hiiragi283.api.multiblock

import io.github.hiiragi283.api.extension.getOrNull
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.multiblock.hatch.HTHatchType
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.RenderLayers
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.block.BlockRenderManager
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.Properties
import net.minecraft.text.LiteralText
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.*
import java.util.function.BiPredicate

class HTMultiblockShape(private val posMap: Map<BlockPos, HTBlockPredicate>) : BiPredicate<World, BlockPos> {
    companion object {
        // https://github.com/AztechMC/Modern-Industrialization/blob/1.20.x/src/main/java/aztech/modern_industrialization/machines/multiblocks/ShapeMatcher.java#L63
        @JvmStatic
        fun toAbsolutePos(originPos: BlockPos, originDirection: Direction?, relativePos: BlockPos): BlockPos = when (originDirection) {
            Direction.SOUTH -> BlockPos(-relativePos.x, relativePos.y, -relativePos.z)
            Direction.WEST -> BlockPos(relativePos.z, relativePos.y, -relativePos.x)
            Direction.EAST -> BlockPos(-relativePos.z, relativePos.y, relativePos.x)
            else -> relativePos
        }.add(originPos)

        @JvmField
        val REGISTRY: Registry<HTMultiblockShape> = FabricRegistryBuilder.createSimple(
            HTMultiblockShape::class.java,
            HTModuleType.API.id("multiblock_shape"),
        )
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister()
    }

    private fun getAbsolutePattern(originPos: BlockPos, originDirection: Direction? = null): Map<BlockPos, HTBlockPredicate> =
        posMap.map { (pos: BlockPos, predicate: HTBlockPredicate) ->
            toAbsolutePos(
                originPos,
                originDirection,
                pos,
            ) to predicate
        }.toMap()

    override fun test(world: World, originPos: BlockPos): Boolean = test(world, originPos, null)

    fun test(world: World, originPos: BlockPos, player: PlayerEntity?): Boolean {
        if (world.isClient || world !is ServerWorld) return false
        val state: BlockState = world.getBlockState(originPos)
        val direction: Direction? = state.getOrNull(Properties.HORIZONTAL_FACING)
        val absolutePattern: Map<BlockPos, HTBlockPredicate> = getAbsolutePattern(originPos, direction)
        val hatchArray: IntArray = intArrayOf(0, 0, 0, 0)
        // Check block predicates
        for ((pos: BlockPos, predicate: HTBlockPredicate) in absolutePattern) {
            if (!predicate.test(world, pos)) {
                player?.sendMessage(LiteralText("Not matching condition; ${predicate.json} at $pos"), false)
                return false
            }
            // Add hatch count
            predicate.getHatch(world, pos)?.type?.ordinal?.let { hatchArray[it]++ }
        }
        // Check hatch count
        HTHatchType.entries.forEach { type ->
            if (hatchArray[type.ordinal] < hatchArray[type.ordinal]) {
                player?.sendMessage(
                    LiteralText(
                        "Required count of $type is ${hatchArray[type.ordinal]} but only ${hatchArray[type.ordinal]} was detected!",
                    ),
                    false,
                )
                return false
            }
        }
        player?.sendMessage(LiteralText("The structure is valid!"), true)
        return true
    }

    @Environment(EnvType.CLIENT)
    fun render(
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        world: World,
        originDirection: Direction? = null,
    ) {
        val dummyPos = BlockPos(0, 260, 0)
        val blockRenderManager: BlockRenderManager = MinecraftClient.getInstance().blockRenderManager
        getAbsolutePattern(BlockPos.ORIGIN, originDirection).forEach { (pos: BlockPos, predicate: HTBlockPredicate) ->
            predicate.getPreviewState(world)?.let { state ->
                matrixStack.push()
                matrixStack.translate(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                matrixStack.translate(0.125, 0.125, 0.125)
                matrixStack.scale(0.75f, 0.75f, 0.75f)
                val consumer: VertexConsumer = vertexConsumerProvider.getBuffer(RenderLayers.getBlockLayer(state))
                blockRenderManager.renderBlock(state, dummyPos, world, matrixStack, consumer, false, Random())
                matrixStack.pop()
            }
        }
    }

    class Builder {
        private val posMap: MutableMap<BlockPos, HTBlockPredicate> = mutableMapOf()
        // private val hatchArray: IntArray = intArrayOf(0, 0, 0, 0)

        fun build() = HTMultiblockShape(posMap)

        //    Predicate    //

        fun addPillar(
            x: Int,
            yRange: IntRange,
            z: Int,
            predicate: HTBlockPredicate,
        ) = apply {
            for (y: Int in yRange) {
                add(x, y, z, predicate)
            }
        }

        fun addLayer(
            xRange: IntRange,
            y: Int,
            zRange: IntRange,
            predicate: HTBlockPredicate,
        ) = apply {
            for (x: Int in xRange) {
                for (z: Int in zRange) {
                    add(x, y, z, predicate)
                }
            }
        }

        fun addHollow(
            xRange: IntRange,
            y: Int,
            zRange: IntRange,
            predicate: HTBlockPredicate,
        ) = apply {
            for (x: Int in xRange) {
                for (z: Int in zRange) {
                    if (x == xRange.first || x == xRange.last || z == zRange.first || z == zRange.last) {
                        add(x, y, z, predicate)
                    }
                }
            }
        }

        fun add(
            x: Int,
            y: Int,
            z: Int,
            predicate: HTBlockPredicate,
        ): Builder = add(BlockPos(x, y, z), predicate)

        private fun add(pos: BlockPos, predicate: HTBlockPredicate): Builder = apply {
            posMap[pos] = predicate
        }

        fun remove(x: Int, y: Int, z: Int): Builder = remove(BlockPos(x, y, z))

        private fun remove(pos: BlockPos): Builder = apply {
            posMap.remove(pos)
        }

        //    Hatch    //

        /*fun setItemInput(count: Int) = apply {
            hatchArray[0] = count
        }

        fun setItemOutput(count: Int) = apply {
            hatchArray[1] = count
        }

        fun setFluidInput(count: Int) = apply {
            hatchArray[2] = count
        }

        fun setFluidOutput(count: Int) = apply {
            hatchArray[3] = count
        }*/
    }
}
