package io.github.hiiragi283.material.common.screen

import io.github.cottonmc.cotton.gui.widget.WButton
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.screen.HTScreenHandler
import io.github.hiiragi283.material.common.HTMaterials
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.CraftingResultInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandlerContext

class HTMaterialDictionaryScreenHandler(
    syncId: Int,
    playerInventory: PlayerInventory,
    private val context: ScreenHandlerContext = ScreenHandlerContext.EMPTY,
) : HTScreenHandler(HTMaterials.screenHandlerType, syncId, playerInventory) {

    constructor(syncId: Int, player: PlayerEntity, context: ScreenHandlerContext = ScreenHandlerContext.EMPTY) : this(
        syncId,
        player.inventory,
        context
    )

    private val input = SimpleInventory(1)
    private val output = CraftingResultInventory()
    private var entries: List<Item> = listOf()
    private var currentIndex: Int = 0

    init {
        val root = WGridPanel()
        setRootPanel(root)
        // Input
        root.add(WItemSlot.of(input, 0), 1, 2)
        // Up Button
        root.add(WButton(ItemIcon(Items.RED_DYE)).setOnClick {
            updateEntries()
            when {
                currentIndex !in entries.indices -> currentIndex = 0
                else -> {
                    val indexAdded: Int = currentIndex + 1
                    currentIndex = (when {
                        indexAdded >= entries.size -> 0
                        else -> indexAdded
                    })
                }
            }
            HTLogger.debug { it.info("Current index; $currentIndex in ${entries.indices}") }
        }, 4, 1)
        // Down Button
        root.add(WButton(ItemIcon(Items.LIME_DYE)).setOnClick {
            updateEntries()
            when {
                currentIndex !in entries.indices -> currentIndex = entries.lastIndex
                else -> {
                    val indexRemoved: Int = currentIndex - 1
                    currentIndex = (when {
                        indexRemoved < 0 -> entries.lastIndex
                        else -> indexRemoved
                    })
                }
            }
            HTLogger.debug { it.info("Current index; $currentIndex in ${entries.indices}") }
        }, 4, 3)
        // Output
        root.add(WItemSlot.of(output, 0).setInsertingAllowed(false), 7, 2)
        // Player Inventory
        root.add(WPlayerInvPanel(playerInventory), 0, 4)
        root.validate(this)
    }

    private fun updateEntries() {
        val stackIn: ItemStack = input.getStack(0)
        if (stackIn.isEmpty) return
        entries = HTApiHolder.Material
            .apiInstance
            .materialItemManager
            .convertibleItems(stackIn.item)
            .toList()
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        context.run { world, _ -> dropInventory(player, world, input) }
    }

}