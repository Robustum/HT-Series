package io.github.hiiragi283.api.material.property

import io.github.hiiragi283.api.material.HTMaterial

fun interface HTTooltipProperty {
    fun appendTooltip(context: HTMaterial.TooltipContext)
}
