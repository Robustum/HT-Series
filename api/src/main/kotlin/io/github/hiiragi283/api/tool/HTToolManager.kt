package io.github.hiiragi283.api.tool

import io.github.hiiragi283.mixin.AxeItemAccessor
import io.github.hiiragi283.mixin.HoeItemAccessor
import io.github.hiiragi283.mixin.ShovelItemAccessor
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.block.Material
import net.minecraft.item.ToolMaterial
import java.util.concurrent.ConcurrentHashMap

object HTToolManager {
    private val blockLevels: MutableMap<HTToolKey, MutableMap<Block, Int>> = ConcurrentHashMap()
    private val materialLevels: MutableMap<HTToolKey, MutableMap<Material, Int>> = ConcurrentHashMap()

    fun canMine(tool: HTToolKey, state: BlockState, toolMaterial: ToolMaterial): Boolean =
        getMiningLevel(tool, state) <= toolMaterial.miningLevel

    fun getMiningLevel(tool: HTToolKey, state: BlockState): Int =
        blockLevels[tool]?.get(state.block) ?: materialLevels[tool]?.get(state.material) ?: 0

    fun setMiningLevel(tool: HTToolKey, block: Block, level: Int = 0) {
        blockLevels.computeIfAbsent(tool) { ConcurrentHashMap() }[block] = level
    }

    fun setMiningLevel(tool: HTToolKey, material: Material, level: Int = 0) {
        materialLevels.computeIfAbsent(tool) { ConcurrentHashMap() }[material] = level
    }

    init {
        // Axe
        AxeItemAccessor.getEFFECTIVE_BLOCKS().forEach { block: Block ->
            setMiningLevel(HTToolKeys.AXE, block)
        }
        setMiningLevel(HTToolKeys.AXE, Material.BAMBOO)
        setMiningLevel(HTToolKeys.AXE, Material.GOURD)
        setMiningLevel(HTToolKeys.AXE, Material.NETHER_WOOD)
        setMiningLevel(HTToolKeys.AXE, Material.PLANT)
        setMiningLevel(HTToolKeys.AXE, Material.REPLACEABLE_PLANT)
        setMiningLevel(HTToolKeys.AXE, Material.WOOD)
        // Hoe
        HoeItemAccessor.getEFFECTIVE_BLOCKS().forEach { block: Block ->
            setMiningLevel(HTToolKeys.HOE, block)
        }
        // Pickaxe
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.ANCIENT_DEBRIS, 3)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.CRYING_OBSIDIAN, 3)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.DIAMOND_BLOCK, 2)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.DIAMOND_ORE, 2)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.EMERALD_BLOCK, 2)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.EMERALD_ORE, 2)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.GOLD_BLOCK, 2)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.GOLD_ORE, 2)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.IRON_BLOCK, 1)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.IRON_ORE, 1)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.LAPIS_BLOCK, 1)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.LAPIS_ORE, 1)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.NETHER_GOLD_ORE)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.NETHERITE_BLOCK, 3)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.OBSIDIAN, 3)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.REDSTONE_ORE, 2)
        setMiningLevel(HTToolKeys.PICKAXE, Blocks.RESPAWN_ANCHOR, 3)
        setMiningLevel(HTToolKeys.PICKAXE, Material.METAL)
        setMiningLevel(HTToolKeys.PICKAXE, Material.REPAIR_STATION)
        setMiningLevel(HTToolKeys.PICKAXE, Material.STONE)
        // Shovel
        ShovelItemAccessor.getEFFECTIVE_BLOCKS().forEach { block: Block ->
            setMiningLevel(HTToolKeys.SHOVEL, block)
        }
        setMiningLevel(HTToolKeys.SHOVEL, Blocks.SNOW)
        setMiningLevel(HTToolKeys.SHOVEL, Blocks.SNOW_BLOCK)
        // Sword
        setMiningLevel(HTToolKeys.SWORD, Blocks.COBWEB)
        setMiningLevel(HTToolKeys.SWORD, Material.GOURD)
        setMiningLevel(HTToolKeys.SWORD, Material.LEAVES)
        setMiningLevel(HTToolKeys.SWORD, Material.MOSS_BLOCK)
        setMiningLevel(HTToolKeys.SWORD, Material.PLANT)
        setMiningLevel(HTToolKeys.SWORD, Material.REPLACEABLE_PLANT)
    }
}
