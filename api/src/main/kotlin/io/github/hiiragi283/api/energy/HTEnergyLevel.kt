package io.github.hiiragi283.api.energy

import com.mojang.serialization.Codec
import io.github.hiiragi283.api.extension.enumCodec

enum class HTEnergyLevel(val canSupplyEnergy: Boolean) {
    OFF(false),
    LOW(true),
    MEDIUM(true),
    HIGH(true),
    EXTREME(true),
    ;

    companion object {
        @JvmField
        val CODEC: Codec<HTEnergyLevel> = enumCodec(HTEnergyLevel::valueOf)
    }
}
