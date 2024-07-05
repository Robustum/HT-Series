package io.github.hiiragi283.api.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.block.InventoryProvider
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemPlacementContext
import net.minecraft.item.ItemStack
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.WorldAccess

abstract class HTItemConvertingBlock(settings: Settings) : HorizontalFacingBlock(settings), InventoryProvider {
    companion object {
        @JvmStatic
        fun dropUnifiedStack(
            world: World,
            pos: BlockPos,
            direction: Direction,
            unifiedStack: ItemStack,
        ) {
            if (world.isClient) return
            val offsetPos: BlockPos = pos.offset(direction)
            ItemEntity(
                world,
                offsetPos.x.toDouble() + 0.5,
                offsetPos.y.toDouble() + 0.5,
                offsetPos.z.toDouble() + 0.5,
                unifiedStack,
            ).apply {
                setPickupDelay(0)
                velocity = Vec3d.ZERO
                world.spawnEntity(this)
            }
        }
    }

    init {
        defaultState = stateManager.defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState =
        defaultState.with(Properties.HORIZONTAL_FACING, ctx.playerFacing.opposite)

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Properties.HORIZONTAL_FACING)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult,
    ): ActionResult {
        if (hand == Hand.MAIN_HAND) {
            val stack: ItemStack = player.getStackInHand(hand)
            val stacks: List<ItemStack> = convertStack(world, pos, stack)
            val direction: Direction = state.get(Properties.HORIZONTAL_FACING)
            stacks.forEach { dropUnifiedStack(world, pos, direction, it) }
            player.setStackInHand(hand, ItemStack.EMPTY)
            return ActionResult.success(world.isClient)
        }
        return ActionResult.PASS
    }

    abstract fun convertStack(world: WorldAccess, pos: BlockPos, stack: ItemStack): List<ItemStack>

    //    InventoryProvider    //

    override fun getInventory(state: BlockState, world: WorldAccess, pos: BlockPos): SidedInventory = LibraryInventory(world, pos, state)

    private class LibraryInventory(val world: WorldAccess, val pos: BlockPos, val state: BlockState) :
        SidedInventory {
        val direction: Direction = state.get(Properties.HORIZONTAL_FACING)
        val convertingBlock: HTItemConvertingBlock = state.block as HTItemConvertingBlock

        override fun clear() = Unit

        override fun size(): Int = 0

        override fun isEmpty(): Boolean = true

        override fun getStack(slot: Int): ItemStack = ItemStack.EMPTY

        override fun removeStack(slot: Int, amount: Int): ItemStack = ItemStack.EMPTY

        override fun removeStack(slot: Int): ItemStack = ItemStack.EMPTY

        override fun setStack(slot: Int, stack: ItemStack) {
            val stacks = convertingBlock.convertStack(world, pos, stack)
            (world as? World)?.run {
                stacks.forEach { dropUnifiedStack(this, pos, direction, it) }
            }
        }

        override fun markDirty() = Unit

        override fun canPlayerUse(player: PlayerEntity): Boolean = false

        //    SidedInventory    //

        override fun getAvailableSlots(side: Direction): IntArray = if (side == direction) intArrayOf() else intArrayOf(0)

        override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean = dir != direction && world is World

        override fun canExtract(slot: Int, stack: ItemStack, dir: Direction?): Boolean = false
    }
}
