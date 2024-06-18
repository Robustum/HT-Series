package io.github.hiiragi283.api.block

import io.github.hiiragi283.api.block.HTMaterialStorageBlock.Type
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.Material
import net.minecraft.item.Item
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.tag.Tag
import net.minecraft.text.MutableText
import net.minecraft.block.Block as MCBlock

private fun buildSettings(type: Type, miningLevel: Int): FabricBlockSettings = FabricBlockSettings.of(type.material)
    .strength(5.0f, 5.0f)
    .sounds(type.soundCategory)
    .apply {
        if (miningLevel > 0) {
            requiresTool()
            breakByTool(type.miningTool, miningLevel)
        } else {
            breakByHand(true)
        }
    }

class HTMaterialStorageBlock(type: Type, miningLevel: Int = 1) : MCBlock(buildSettings(type, miningLevel)) {
    override fun getName(): MutableText = asItem().name as MutableText

    //    HTShapeType    //

    enum class Type(
        val modelName: String,
        val material: Material,
        val soundCategory: BlockSoundGroup,
        val miningTool: Tag<Item>,
    ) {
        DEFAULT("solid", Material.STONE, BlockSoundGroup.STONE, FabricToolTags.PICKAXES),
        GEM("gem", Material.GLASS, BlockSoundGroup.GLASS, FabricToolTags.PICKAXES),
        METAL_DULL("dull", Material.METAL, BlockSoundGroup.METAL, FabricToolTags.PICKAXES),
        METAL_SHINY("shiny", Material.METAL, BlockSoundGroup.METAL, FabricToolTags.PICKAXES),
        WOOD("solid", Material.WOOD, BlockSoundGroup.WOOD, FabricToolTags.AXES),
    }
}
