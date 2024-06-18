package io.github.hiiragi283.api.material.property

import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.gen.GenerationStep
import net.minecraft.world.gen.feature.ConfiguredFeature

interface HTConfiguredFeatureProperty {
    val configuredFeature: ConfiguredFeature<*, *>

    val registryKey: RegistryKey<ConfiguredFeature<*, *>>

    val step: GenerationStep.Feature

    fun checkBiome(context: BiomeSelectionContext): Boolean
}
