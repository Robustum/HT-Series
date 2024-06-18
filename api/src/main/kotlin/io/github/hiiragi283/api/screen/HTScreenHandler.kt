package io.github.hiiragi283.api.screen

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.screen.PropertyDelegate
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType

abstract class HTScreenHandler(
    type: ScreenHandlerType<*>,
    syncId: Int,
    playerInventory: PlayerInventory,
    context: ScreenHandlerContext = ScreenHandlerContext.EMPTY,
    blockInventory: Inventory,
    propertyDelegate: PropertyDelegate,
) : SyncedGuiDescription(type, syncId, playerInventory, blockInventory, propertyDelegate) {
    /*companion object {
        @JvmStatic
        fun getBlockTank(context: ScreenHandlerContext): HTSlottedStorage<FluidVariant> = getBlockTank(context, 0)

        @JvmStatic
        fun getBlockTank(context: ScreenHandlerContext, size: Int): HTSlottedStorage<FluidVariant> = getBlockTank(
            context,
        ) { HTSidedStorageBuilder(size).build(HTSlottedStorage.Companion::ofFluid) }

        private fun getBlockTank(
            context: ScreenHandlerContext,
            fallback: () -> HTSlottedStorage<FluidVariant>,
        ): HTSlottedStorage<FluidVariant> = context.get { world, pos ->
            (world.getBlockEntity(pos) as? HTStorageProvider)
                ?.getFluidStorage(null)
                ?: fallback()
        }.orElseGet(fallback)
    }*/
}
