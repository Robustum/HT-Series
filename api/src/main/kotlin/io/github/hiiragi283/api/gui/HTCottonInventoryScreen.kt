package io.github.hiiragi283.api.gui

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class HTCottonInventoryScreen<T : SyncedGuiDescription>(
    description: T,
    playerInventory: PlayerInventory,
    title: Text,
) : CottonInventoryScreen<T>(description, playerInventory.player, title) {
    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        val result: Boolean = super.mouseClicked(mouseX, mouseY, mouseButton)
        val containerX: Int = mouseX.toInt() - x
        val containerY: Int = mouseY.toInt() - y
        return if (containerX < width && containerY < height) {
            if (lastResponder == null) {
                lastResponder = description.doMouseDown(containerX, containerY, mouseButton)
            }
            result
        } else {
            result
        }
    }
}
