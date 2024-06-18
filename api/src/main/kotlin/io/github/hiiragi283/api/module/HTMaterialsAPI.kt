package io.github.hiiragi283.api.module

import io.github.hiiragi283.api.fluid.phase.HTMaterialFluidManager
import io.github.hiiragi283.api.item.shape.HTMaterialItemManager
import io.github.hiiragi283.api.item.shape.HTShapeRegistry
import io.github.hiiragi283.api.item.shape.HTShapedMaterial
import io.github.hiiragi283.api.material.HTMaterialRegistry
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

interface HTMaterialsAPI {
    val itemGroup: ItemGroup
    val shapeRegistry: HTShapeRegistry
    val materialRegistry: HTMaterialRegistry
    // val contentRegistry: HTMaterialContentRegistry

    val materialFluidManager: HTMaterialFluidManager
    val materialItemManager: HTMaterialItemManager

    fun forEachLazyPart(action: (HTShapedMaterial.Lazy) -> Unit) {
        materialRegistry.keys.forEach { materialKey ->
            shapeRegistry.keys.map { shapeKey ->
                HTShapedMaterial.lazy(materialKey, shapeKey)
            }.forEach(action)
        }
    }

    fun forEachDirectPart(action: (HTShapedMaterial.Direct) -> Unit) {
        materialRegistry.values.forEach { material ->
            shapeRegistry.values.map { shape ->
                HTShapedMaterial.direct(material, shape)
            }.forEach(action)
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
