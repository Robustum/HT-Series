package io.github.hiiragi283.api.tool

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import java.util.concurrent.ConcurrentHashMap

object HTBlockReformingRegistry {
    //    Hoe Tilling    //

    private val TILLED_BLOCKS: MutableMap<Block, BlockState> = ConcurrentHashMap()

    @JvmStatic
    fun getTilled(block: Block): BlockState? = TILLED_BLOCKS[block]

    @JvmStatic
    fun setTilled(block: Block, block1: Block) {
        setTilled(block, block1.defaultState)
    }

    @JvmStatic
    fun setTilled(block: Block, state: BlockState) {
        TILLED_BLOCKS[block] = state
    }

    //    Shovel Pathing    //

    private val PATH_BLOCKS: MutableMap<Block, BlockState> = ConcurrentHashMap()

    @JvmStatic
    fun getPath(block: Block): BlockState? = PATH_BLOCKS[block]

    @JvmStatic
    fun setPath(block: Block, block1: Block) {
        setPath(block, block1.defaultState)
    }

    @JvmStatic
    fun setPath(block: Block, state: BlockState) {
        PATH_BLOCKS[block] = state
    }

    init {
        setTilled(Blocks.GRASS_BLOCK, Blocks.FARMLAND)
        setTilled(Blocks.GRASS_PATH, Blocks.FARMLAND)
        setTilled(Blocks.DIRT, Blocks.FARMLAND)
        setTilled(Blocks.COARSE_DIRT, Blocks.DIRT)
        setPath(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH)
    }
}
