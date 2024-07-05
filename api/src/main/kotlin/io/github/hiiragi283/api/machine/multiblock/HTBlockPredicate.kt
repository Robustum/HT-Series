package io.github.hiiragi283.api.machine.multiblock

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.mojang.serialization.JsonOps
import io.github.hiiragi283.api.extension.addProperty
import io.github.hiiragi283.api.extension.buildJson
import io.github.hiiragi283.api.extension.encodeResult
import io.github.hiiragi283.api.extension.toJsonArray
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.tag.Tag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import java.util.*
import java.util.function.BiPredicate

sealed class HTBlockPredicate : BiPredicate<World, BlockPos> {
    protected abstract val previewStates: List<BlockState>

    private fun getIndex(world: World): Int = when (val size: Int = previewStates.size) {
        0 -> 0
        1 -> 0
        else -> ((world.time % (20 * size)) / 20).toInt()
    }

    fun getPreviewState(world: World): BlockState? = previewStates.getOrNull(getIndex(world))

    abstract val json: JsonElement

    // fun getHatch(world: World, pos: BlockPos): HTHatchProvider? = world.getBlockState(pos).block as? HTHatchProvider

    // protected abstract fun internalTest(world: World, pos: BlockPos): Boolean

    companion object {
        @JvmStatic
        fun any(): HTBlockPredicate = Any

        @JvmStatic
        fun of(vararg block: Block): HTBlockPredicate = Simple(*block)

        @JvmStatic
        fun ofTag(tag: Tag<Block>): HTBlockPredicate = Group(tag)

        @JvmStatic
        fun ofStates(states: List<BlockState>): HTBlockPredicate = States(states)
    }

    private data object Any : HTBlockPredicate() {
        override val previewStates: List<BlockState> = listOf(Blocks.AIR.defaultState)

        override val json: JsonElement = JsonNull.INSTANCE

        override fun test(world: World, pos: BlockPos): Boolean = true
    }

    private data class Simple(private val blocks: List<Block>) : HTBlockPredicate() {
        constructor(vararg blocks: Block) : this(blocks.toList())

        override val previewStates: List<BlockState> = blocks.map(Block::getDefaultState)

        override val json: JsonElement = blocks
            .map(Registry.BLOCK::getId)
            .map { it.toString() }
            .toJsonArray(::JsonPrimitive)

        override fun test(world: World, pos: BlockPos): Boolean = world.getBlockState(pos).block in blocks
    }

    private data class Group(private val tag: Tag<Block>) : HTBlockPredicate() {
        override val previewStates: List<BlockState>
            get() = tag.values().map(Block::getDefaultState)

        override val json: JsonElement = buildJson {
            (tag as? Tag.Identified<Block>)?.id?.let { addProperty("tag", it) }
        }

        override fun test(world: World, pos: BlockPos): Boolean = world.getBlockState(pos).isIn(tag)
    }

    private data class States(override val previewStates: List<BlockState>) : HTBlockPredicate() {
        override val json: JsonElement = previewStates
            .map { BlockState.CODEC.encodeResult(JsonOps.INSTANCE, it) }
            .mapNotNull(Result<JsonElement>::getOrNull)
            .toJsonArray()

        override fun test(world: World, pos: BlockPos): Boolean = world.getBlockState(pos) in previewStates
    }
}
