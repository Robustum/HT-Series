package io.github.hiiragi283.engineering.common.block.entity

import io.github.hiiragi283.api.block.entity.HTAbstractMachineBlockEntity
import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.storage.HTStorageSide
import io.github.hiiragi283.engineering.common.init.HEBlockEntityTypes
import io.github.hiiragi283.engineering.common.init.HERecipeTypes
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

class HTCauldronBlockEntity : HTAbstractMachineBlockEntity(HEBlockEntityTypes.CAULDRON, HERecipeTypes.CAULDRON) {
    //    NamedScreenHandlerFactory    //

    override fun getDisplayName(): Text = TranslatableText("block.ht_engineering.primitive_blast_furnace")

    //    HTEnergySourceFinder    //

    override fun getValidSide(type: HTEnergyType): HTStorageSide = when (type) {
        HTEnergyType.COOLANT -> HTStorageSide.DOWN
        HTEnergyType.ELECTRICITY -> HTStorageSide.NONE
        HTEnergyType.ENCHANTMENT -> HTStorageSide.NONE
        HTEnergyType.HEAT -> HTStorageSide.DOWN
        HTEnergyType.SOUL -> HTStorageSide.DOWN
    }
}
