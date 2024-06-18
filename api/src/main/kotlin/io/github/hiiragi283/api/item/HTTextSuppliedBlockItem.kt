package io.github.hiiragi283.api.item

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class HTTextSuppliedBlockItem(
    block: Block,
    settings: Settings,
    private val textSupplier: () -> Text,
) : BlockItem(block, settings) {
    override fun getName(): Text = textSupplier()

    override fun getName(stack: ItemStack): Text = textSupplier()
}
