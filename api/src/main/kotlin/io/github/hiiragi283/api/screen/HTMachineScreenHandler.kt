package io.github.hiiragi283.api.screen

import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel
import io.github.hiiragi283.api.module.HTModuleType
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.util.Identifier

class HTMachineScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    context: ScreenHandlerContext = ScreenHandlerContext.EMPTY,
) : HTScreenHandler(
        TYPE,
        syncId,
        playerInventory,
        getBlockInventory(context, 5),
        getBlockPropertyDelegate(context, 2),
    ) {
    init {
        val root = WGridPanel()
        setRootPanel(root)
        // Item Input 1
        root.add(WItemSlot.of(blockInventory, 0), 1, 2)
        // Item Input 2
        root.add(WItemSlot.of(blockInventory, 1), 2, 2)
        // Catalyst 1
        root.add(WItemSlot.of(blockInventory, 2), 4, 1)
        // Arrow
        root.add(
            WBar(
                Identifier("textures/block/white_wool.png"),
                Identifier("textures/block/red_wool.png"),
                0,
                1,
                WBar.Direction.RIGHT,
            ),
            4,
            2,
        )
        // Item Output 1
        root.add(WItemSlot.of(blockInventory, 3).setInsertingAllowed(false), 6, 2)
        // Item Output 2
        root.add(WItemSlot.of(blockInventory, 4).setInsertingAllowed(false), 7, 2)
        // Player Inventory
        root.add(WPlayerInvPanel(playerInventory), 0, 4)
        root.validate(this)
    }

    companion object {
        @JvmField
        val TYPE: ScreenHandlerType<HTMachineScreenHandler> = ScreenHandlerRegistry.registerSimple(
            HTModuleType.API.id("machine"),
            ::HTMachineScreenHandler,
        )
    }
}