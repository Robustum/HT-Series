package io.github.hiiragi283.api.block.entity

import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.storage.HTStorageSide
import net.minecraft.util.math.BlockPos

interface HTEnergySourceFinder {
    fun pos(): BlockPos

    fun getValidSide(type: HTEnergyType): HTStorageSide
}
