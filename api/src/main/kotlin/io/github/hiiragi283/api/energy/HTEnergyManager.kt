package io.github.hiiragi283.api.energy

import io.github.hiiragi283.api.extension.lowerName
import io.github.hiiragi283.api.module.HTModuleType
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

@Suppress("UnstableApiUsage")
object HTEnergyManager {
    @JvmStatic
    val blocks: Map<HTEnergyType, BlockApiLookup<HTEnergyLevel, Direction>>
        get() = blocks1
    private val blocks1: MutableMap<HTEnergyType, BlockApiLookup<HTEnergyLevel, Direction>> = mutableMapOf()

    @JvmStatic
    val items: Map<HTEnergyType, ItemApiLookup<HTEnergyLevel, ContainerItemContext>>
        get() = items1
    private val items1: MutableMap<HTEnergyType, ItemApiLookup<HTEnergyLevel, ContainerItemContext>> = mutableMapOf()

    //    Block    //

    @JvmStatic
    fun getBlockLookup(type: HTEnergyType): BlockApiLookup<HTEnergyLevel, Direction> = blocks[type]!!

    @JvmStatic
    fun getLevel(
        type: HTEnergyType,
        world: World,
        pos: BlockPos,
        side: Direction,
    ): HTEnergyLevel? = getBlockLookup(type).find(world, pos, world.getBlockState(pos), world.getBlockEntity(pos), side)

    @JvmStatic
    fun getLevelOrOff(
        type: HTEnergyType,
        world: World,
        pos: BlockPos,
        side: Direction,
    ): HTEnergyLevel = getLevel(type, world, pos, side) ?: HTEnergyLevel.OFF

    @JvmStatic
    fun canSupplyPower(
        type: HTEnergyType,
        world: World,
        pos: BlockPos,
        side: Direction,
    ): Boolean = getLevel(type, world, pos, side)?.canSupplyEnergy == true

    //    Item    //

    @JvmStatic
    fun getItemLookup(type: HTEnergyType): ItemApiLookup<HTEnergyLevel, ContainerItemContext> = items[type]!!

    @JvmOverloads
    @JvmStatic
    fun getLevel(
        type: HTEnergyType,
        stack: ItemStack,
        context: ContainerItemContext = ContainerItemContext.withInitial(stack),
    ): HTEnergyLevel? = getItemLookup(type).find(stack, context)

    @JvmOverloads
    @JvmStatic
    fun getLevelOrOff(
        type: HTEnergyType,
        stack: ItemStack,
        context: ContainerItemContext = ContainerItemContext.withInitial(stack),
    ): HTEnergyLevel = getLevel(type, stack, context) ?: HTEnergyLevel.OFF

    @JvmOverloads
    @JvmStatic
    fun canSupplyPower(
        type: HTEnergyType,
        stack: ItemStack,
        context: ContainerItemContext = ContainerItemContext.withInitial(stack),
    ): Boolean = getLevel(type, stack, context)?.canSupplyEnergy == true

    //    Initialization    //

    init {
        HTEnergyType.entries.forEach(HTEnergyManager::createApiLookups)
    }

    private fun createApiLookups(type: HTEnergyType) {
        createBlockApiLookup(type)
        createItemApiLookup(type)
    }

    private fun createBlockApiLookup(type: HTEnergyType): BlockApiLookup<HTEnergyLevel, Direction> = BlockApiLookup.get(
        HTModuleType.ENGINEERING.id("sided_${type.lowerName}_level"),
        HTEnergyLevel::class.java,
        Direction::class.java,
    ).apply {
        registerFallback { world, _, _, _, _ ->
            check(!world.isClient) { "Sided ${type.lowerName} provider may only be queried for a server world." }
            null
        }
        registerFallback { _, _, state, blockEntity, _ ->
            // HTEnergyProvider
            (state.block as? HTEnergyProvider)?.getEnergyLevel(type)
            (blockEntity as? HTEnergyProvider)?.getEnergyLevel(type)
            // Tag
            HTEnergyLevel.entries.forEach { level ->
                if (state.isIn(type.getBlockTag(level))) {
                    return@registerFallback level
                }
            }
            null
        }
        blocks1[type] = this
    }

    private fun createItemApiLookup(type: HTEnergyType): ItemApiLookup<HTEnergyLevel, ContainerItemContext> = ItemApiLookup.get(
        HTModuleType.ENGINEERING.id("${type.lowerName}_level"),
        HTEnergyLevel::class.java,
        ContainerItemContext::class.java,
    ).apply {
        registerFallback { itemStack, _ ->
            HTEnergyLevel.entries.forEach { level ->
                if (type.getItemTag(level).contains(itemStack.item)) {
                    return@registerFallback level
                }
            }
            null
        }
        items1[type] = this
    }
}
