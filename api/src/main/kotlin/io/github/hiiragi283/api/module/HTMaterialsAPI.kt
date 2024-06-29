package io.github.hiiragi283.api.module

import io.github.hiiragi283.api.extension.runCatchAndLog
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.fluid.phase.HTMaterialFluidManager
import io.github.hiiragi283.api.fluid.phase.HTPhasedMaterial
import io.github.hiiragi283.api.item.shape.HTMaterialItemManager
import io.github.hiiragi283.api.item.shape.HTShapeRegistry
import io.github.hiiragi283.api.item.shape.HTShapedMaterial
import io.github.hiiragi283.api.material.HTMaterialRegistry
import io.github.hiiragi283.api.material.content.HTMaterialContentManager
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

interface HTMaterialsAPI {
    val itemGroup: ItemGroup
    val shapeRegistry: HTShapeRegistry
    val materialRegistry: HTMaterialRegistry

    val materialContentManager: HTMaterialContentManager
    val materialFluidManager: HTMaterialFluidManager
    val materialItemManager: HTMaterialItemManager

    fun forEachPhasedMaterial(action: (HTPhasedMaterial) -> Unit) {
        materialRegistry.keys.forEach { materialKey ->
            HTFluidPhase.entries.map { phase ->
                HTPhasedMaterial(materialKey, phase)
            }.forEach { runCatchAndLog { action(it) } }
        }
    }

    fun forEachShapedMaterial(action: (HTShapedMaterial) -> Unit) {
        materialRegistry.keys.forEach { materialKey ->
            shapeRegistry.keys.map { shapeKey ->
                HTShapedMaterial(materialKey, shapeKey)
            }.forEach { runCatchAndLog { action(it) } }
        }
    }

    object RegistryKeys {
        @JvmField
        val ICON: RegistryKey<Item> =
            RegistryKey.of(Registry.ITEM_KEY, HTModuleType.MATERIAL.id("icon"))

        @JvmField
        val DICTIONARY: RegistryKey<Item> =
            RegistryKey.of(Registry.ITEM_KEY, HTModuleType.MATERIAL.id("material_dictionary"))

        @JvmField
        val LIBRARY: RegistryKey<Block> =
            RegistryKey.of(Registry.BLOCK_KEY, HTModuleType.MATERIAL.id("material_library"))
    }
}
