package io.github.hiiragi283.material.compat

import io.github.hiiragi283.api.extension.HTColor
import io.github.hiiragi283.api.extension.averageColor
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.HTMaterialKeys
import io.github.hiiragi283.api.material.HTMaterialRegistry
import io.github.hiiragi283.api.material.composition.HTElements
import io.github.hiiragi283.api.material.composition.HTMaterialComposition
import io.github.hiiragi283.api.material.property.HTMaterialGemType
import io.github.hiiragi283.api.module.HTPlugin

object HMAE2Plugin : HTPlugin.Material {
    override val modId: String = "appliedenergistics2"
    override val priority: Int = 0

    override fun registerMaterial(builder: HTMaterialRegistry.Builder) {
        builder.addGemMaterial(
            HTMaterialKeys.CERTUS_QUARTZ,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1),
            HTMaterialGemType.QUARTZ,
        ).color(averageColor(HTColor.AQUA to 1, HTColor.WHITE to 2))
            .addGemItems(false)
            .removeItem(HTShapeKeys.GEM)
        builder.addGemMaterial(
            HTMaterialKeys.FLUIX,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1),
            HTMaterialGemType.QUARTZ,
        ).color(averageColor(HTColor.BLUE to 2, HTColor.RED to 1))
            .addGemItems(false)
            .removeItem(HTShapeKeys.GEM)
        builder.addStoneMaterial(
            HTMaterialKeys.SKY_STONE,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1),
        ).color(averageColor(HTColor.BLACK, HTColor.GRAY))
            .addItem(HTShapeKeys.PLATE)
    }
}
