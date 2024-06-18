package io.github.hiiragi283.engineering.common.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Fertilizable
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.tag.Tag
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.*

@Suppress("OVERRIDE_DEPRECATION")
class HTFermentableSoilBlock(val replaceTag: () -> Tag<Block>, settings: Settings) : Block(settings), Fertilizable {
    init {
        defaultState = stateManager.defaultState.with(Properties.AGE_3, 0)
    }

    override fun hasRandomTicks(state: BlockState): Boolean = !isMature(state)

    override fun randomTick(
        state: BlockState,
        world: ServerWorld,
        pos: BlockPos,
        random: Random,
    ) {
        Direction.entries
            .map(pos::offset)
            .map(world::getBlockState)
            .forEach { nextState: BlockState ->
                val block: HTFermentableSoilBlock = nextState.block as? HTFermentableSoilBlock ?: return@forEach
                if (block.isMature(nextState)) return@forEach
                addAge(nextState)
            }
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Properties.AGE_3)
    }

    val maxAge: Int = 3

    fun getAge(state: BlockState): Int = state.get(Properties.AGE_3)

    fun setAge(state: BlockState, age: Int): BlockState = state.with(Properties.AGE_3, age)

    fun addAge(state: BlockState): BlockState = state.with(Properties.AGE_3, getAge(state) + 1)

    fun createWithAge(age: Int): BlockState = setAge(defaultState, age)

    fun isMature(state: BlockState): Boolean = getAge(state) == 3

    //    Fertilizable    //

    override fun isFertilizable(
        world: BlockView,
        pos: BlockPos,
        state: BlockState,
        isClient: Boolean,
    ): Boolean = false

    override fun canGrow(
        world: World,
        random: Random,
        pos: BlockPos,
        state: BlockState,
    ): Boolean {
        val states: List<BlockState> = Direction.entries.map(pos::offset).map(world::getBlockState)
        return states.all { !it.isAir } && states.any { it.isIn(replaceTag()) }
    }

    override fun grow(
        world: ServerWorld,
        random: Random,
        pos: BlockPos,
        state: BlockState,
    ) {
        Direction.entries.forEach { direction ->
            val offsetPos: BlockPos = pos.offset(direction)
            val offsetState: BlockState = world.getBlockState(offsetPos)
            if (offsetState.isIn(replaceTag())) {
                world.setBlockState(offsetPos, createWithAge(0))
                return
            }
        }
    }
}
