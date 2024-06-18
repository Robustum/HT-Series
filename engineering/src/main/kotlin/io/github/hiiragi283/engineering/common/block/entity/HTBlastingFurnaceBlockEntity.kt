package io.github.hiiragi283.engineering.common.block.entity

import io.github.hiiragi283.api.block.entity.HTMultiblockControllerBlockEntity
import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.storage.HTStorageSide
import io.github.hiiragi283.engineering.common.init.HEBlockEntityTypes
import io.github.hiiragi283.engineering.common.init.HEMultiblockShapes
import io.github.hiiragi283.engineering.common.init.HERecipeTypes
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

class HTBlastingFurnaceBlockEntity : HTMultiblockControllerBlockEntity(
    HEBlockEntityTypes.PRIMITIVE_BLAST_FURNACE,
    HERecipeTypes.PRIMITIVE_BLAST_FURNACE,
    HEMultiblockShapes.PRIMITIVE_BLAST_FURNACE,
) {
    //    NamedScreenHandlerFactory    //

    override fun getDisplayName(): Text = TranslatableText("block.ht_engineering.primitive_blast_furnace")

    //    HTEnergySourceFinder    //

    override fun getValidSide(type: HTEnergyType): HTStorageSide = when (type) {
        HTEnergyType.HEAT -> HTStorageSide.SIDE
        else -> HTStorageSide.NONE
    }
}
