package io.github.hiiragi283.api.energy

enum class HTEnergyLevel(val canSupplyEnergy: Boolean) {
    OFF(false),
    LOW(true),
    MEDIUM(true),
    HIGH(true),
    EXTREME(true),
}
