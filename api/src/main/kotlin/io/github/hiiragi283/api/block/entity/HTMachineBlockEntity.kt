package io.github.hiiragi283.api.block.entity

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder
import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.extension.buildPropertyDelegate
import io.github.hiiragi283.api.machine.HTMachineType
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.recipe.HTRecipe
import io.github.hiiragi283.api.screen.HTMachineScreenHandler
import io.github.hiiragi283.api.storage.*
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.NbtCompound
import net.minecraft.screen.*
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Tickable
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import kotlin.jvm.optionals.getOrNull

open class HTMachineBlockEntity(open val machineType: HTMachineType<*>) :
    HTBlockEntity(machineType),
    NamedScreenHandlerFactory,
    PropertyDelegateHolder,
    Tickable,
    HTDelegatedInventory,
    HTEnergySourceFinder {
    override fun markDirty() {
        super<HTBlockEntity>.markDirty()
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        parent.writeNbt(nbt)
        return super.writeNbt(nbt)
    }

    override fun fromTag(state: BlockState, tag: NbtCompound) {
        super.fromTag(state, tag)
        parent.readNbt(tag)
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult,
    ): ActionResult = if (world.isClient) {
        ActionResult.SUCCESS
    } else {
        player.openHandledScreen(state.createScreenHandlerFactory(world, pos))
        ActionResult.CONSUME
    }

    //    NamedScreenHandlerFactory    //

    override fun getDisplayName(): Text = machineType.icon().name

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler =
        HTMachineScreenHandler(syncId, inv, ScreenHandlerContext.create(player.world, pos))

    //    PropertyDelegateHolder    //

    open val property: ArrayPropertyDelegate = buildPropertyDelegate(arrayOf(0, 200))

    final override fun getPropertyDelegate(): PropertyDelegate = property

    //    Tickable    //

    override fun tick() {
        onWorldLoaded { world ->
            if (!world.isClient) {
                propertyDelegate[0] = (world.time % 200).toInt()
                if (world.time > 0 && propertyDelegate[0] == 0) {
                    val recipe: HTRecipe = world.recipeManager.getFirstMatch(machineType, this, world)
                        .getOrNull()
                        ?: return@onWorldLoaded
                    HTLogger.debug { it.info("Found recipe: $recipe") }
                    recipe.process(this, world)
                }
            }
        }
    }

    //    HTDelegatedInventory    //

    override val parent = HTSidedInventory(
        HTSidedStorageBuilder(5)
            .set(0, HTStorageIO.INPUT, HTStorageSide.ANY)
            .set(1, HTStorageIO.INPUT, HTStorageSide.ANY)
            .set(2, HTStorageIO.GENERIC, HTStorageSide.NONE)
            .set(3, HTStorageIO.OUTPUT, HTStorageSide.ANY)
            .set(4, HTStorageIO.OUTPUT, HTStorageSide.ANY),
    )

    //    HTEnergySourceFinder    //

    final override fun pos(): BlockPos = pos

    override fun getValidSide(type: HTEnergyType): HTStorageSide = machineType.getValidSide(type)
}
