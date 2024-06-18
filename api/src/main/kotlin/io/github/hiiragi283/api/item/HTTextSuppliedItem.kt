package io.github.hiiragi283.api.item

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class HTTextSuppliedItem(
    settings: Settings,
    private val textSupplier: () -> Text,
) : Item(settings) {
    override fun getName(): Text = textSupplier()

    override fun getName(stack: ItemStack): Text = textSupplier()
}
