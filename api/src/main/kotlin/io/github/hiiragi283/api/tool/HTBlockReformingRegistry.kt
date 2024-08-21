package io.github.hiiragi283.api.tool

import io.github.hiiragi283.mixin.AxeItemAccessor
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks

object HTBlockReformingRegistry {
    private val registry: MutableMap<HTToolClass, MutableMap<Block, BlockState>> = mutableMapOf()

    private fun get(toolClass: HTToolClass, before: Block): BlockState? = registry[toolClass]?.get(before)

    private fun set(toolClass: HTToolClass, before: Block, after: BlockState) {
        registry.computeIfAbsent(toolClass) { mutableMapOf() }[before] = after
    }

    init {
        AxeItemAccessor.getSTRIPPED_BLOCKS().forEach { (before: Block, after: Block) ->
            setStripped(before, after)
        }
        setTilled(Blocks.GRASS_BLOCK, Blocks.FARMLAND)
        setTilled(Blocks.GRASS_PATH, Blocks.FARMLAND)
        setTilled(Blocks.DIRT, Blocks.FARMLAND)
        setTilled(Blocks.COARSE_DIRT, Blocks.DIRT)
        setFlattened(Blocks.GRASS_BLOCK, Blocks.GRASS_PATH)
    }

    //    Axe    //

    @JvmStatic
    fun getStripped(before: Block): BlockState? = get(HTToolClass.AXE, before)

    @JvmStatic
    fun setStripped(before: Block, after: Block) {
        setStripped(before, after.defaultState)
    }

    @JvmStatic
    fun setStripped(before: Block, after: BlockState) {
        set(HTToolClass.AXE, before, after)
    }

    //    Hoe    //

    @JvmStatic
    fun getTilled(before: Block): BlockState? = get(HTToolClass.HOE, before)

    @JvmStatic
    fun setTilled(before: Block, after: Block) {
        setTilled(before, after.defaultState)
    }

    @JvmStatic
    fun setTilled(before: Block, after: BlockState) {
        set(HTToolClass.HOE, before, after)
    }

    //    Shovel    //

    @JvmStatic
    fun getFlattened(before: Block): BlockState? = get(HTToolClass.SHOVEL, before)

    @JvmStatic
    fun setFlattened(before: Block, after: Block) {
        setFlattened(before, after.defaultState)
    }

    @JvmStatic
    fun setFlattened(before: Block, after: BlockState) {
        set(HTToolClass.SHOVEL, before, after)
    }
}
