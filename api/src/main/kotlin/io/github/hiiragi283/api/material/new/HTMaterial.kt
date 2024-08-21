package io.github.hiiragi283.api.material.new

import io.github.hiiragi283.api.extension.HTColor
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.material.composition.HTElement
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.material.type.HTMaterialType
import io.github.hiiragi283.api.property.HTPropertyHolder
import java.awt.Color

class HTMaterial internal constructor(holder: HTPropertyHolder) : HTPropertyHolder by holder {
    val type: HTMaterialType = getOrDefault(HTMaterialProperties.TYPE, HTMaterialType.Solid)
    val color: Color = getOrDefault(HTMaterialProperties.COLOR, HTColor.WHITE)
    val colorOrNull: Color? = get(HTMaterialProperties.COLOR)
    val formula: String? = get(HTMaterialProperties.FORMULA)
    val molar: Double? = get(HTMaterialProperties.MOLAR)
    val component: Map<HTElement, Int> = getOrDefault(HTMaterialProperties.COMPONENT, emptyMap())
    val defaultShape: HTShapeKey? = get(HTMaterialProperties.DEFAULT_ITEM_SHAPE)
}
