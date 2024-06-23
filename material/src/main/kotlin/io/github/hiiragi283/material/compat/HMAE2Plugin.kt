package io.github.hiiragi283.material.compat

import io.github.hiiragi283.api.extension.HTColor
import io.github.hiiragi283.api.extension.averageColor
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.item.shape.HTShapeRegistry
import io.github.hiiragi283.api.material.HTMaterialKeys
import io.github.hiiragi283.api.material.HTMaterialRegistry
import io.github.hiiragi283.api.material.composition.HTElements
import io.github.hiiragi283.api.material.composition.HTMaterialComposition
import io.github.hiiragi283.api.material.type.HTMaterialType
import io.github.hiiragi283.api.module.HTPlugin

object HMAE2Plugin : HTPlugin.Material {
    override val modId: String = "appliedenergistics2"
    override val priority: Int = 0

    override fun registerShape(builder: HTShapeRegistry.Builder) {
        builder.getBuilder(HTShapeKeys.GEM)
            ?.addBlacklist(
                HTMaterialKeys.CERTUS_QUARTZ,
                HTMaterialKeys.FLUIX,
            )
    }

    override fun registerMaterial(builder: HTMaterialRegistry.Builder) {
        builder.createGem(HTMaterialKeys.CERTUS_QUARTZ, HTMaterialType.Gem.QUARTZ)
            .composition(HTMaterialComposition.molecular(HTElements.SiO2 to 1))
            .color(averageColor(HTColor.AQUA to 1, HTColor.WHITE to 2))
        builder.createGem(HTMaterialKeys.FLUIX, HTMaterialType.Gem.QUARTZ)
            .composition(HTMaterialComposition.molecular(HTElements.SiO2 to 1))
            .color(averageColor(HTColor.BLUE to 2, HTColor.RED to 1))
        builder.createStone(HTMaterialKeys.SKY_STONE)
            .composition(HTMaterialComposition.molecular(HTElements.SiO2 to 1))
            .color(averageColor(HTColor.BLACK, HTColor.GRAY))
    }
}
