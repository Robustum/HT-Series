package io.github.hiiragi283.api.material.type

import net.minecraft.block.Material
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.Tag

interface HTBlockProperty {
    val blockMaterial: Material
    val soundGroup: BlockSoundGroup
    val miningTool: Tag<Item>?
    val modelName: String
}
