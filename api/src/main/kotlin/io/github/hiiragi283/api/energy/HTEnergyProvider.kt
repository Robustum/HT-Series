package io.github.hiiragi283.api.energy

fun interface HTEnergyProvider {
    fun getEnergyLevel(type: HTEnergyType): HTEnergyLevel
}
