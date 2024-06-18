package io.github.hiiragi283.api.module

import io.github.hiiragi283.api.fluid.phase.HTMaterialFluidManager
import io.github.hiiragi283.api.item.shape.HTMaterialItemManager
import io.github.hiiragi283.api.item.shape.HTShape
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeRegistry
import io.github.hiiragi283.api.material.HTMaterial
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialRegistry
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

interface HTPlugin {
    val modId: String
    val priority: Int

    fun id(path: String) = Identifier(modId, path)

    interface Material : HTPlugin {
        //    Init    //

        fun registerShape(builder: HTShapeRegistry.Builder) {}

        fun registerMaterial(builder: HTMaterialRegistry.Builder) {}

        // fun registerMaterialContent(builder: HTMaterialContentRegistry.Builder) {}

        fun afterMaterialRegistration(instance: HTMaterialsAPI, isClient: Boolean) {}

        fun bindMaterialWithFluid(builder: HTMaterialFluidManager.Builder) {}

        fun bindMaterialWithItem(builder: HTMaterialItemManager.Builder) {
            registerAllItems(builder)
        }

        fun registerAllItems(builder: HTMaterialItemManager.Builder) {
            HTApiHolder.Material.apiInstance
                .forEachDirectPart { (material: HTMaterial, shape: HTShape) ->
                    Registry.ITEM.get(shape.getRegistryKey(Registry.ITEM_KEY, material, modId))?.run {
                        builder.add(material, shape.key, this)
                    }
                }
        }

        fun addModItem(builder: HTMaterialItemManager.Builder, materialKey: HTMaterialKey, shapeKey: HTShapeKey) {
            builder.add(materialKey, shapeKey, Registry.ITEM.get(shapeKey.get().getId(materialKey, modId)))
        }
    }
}
