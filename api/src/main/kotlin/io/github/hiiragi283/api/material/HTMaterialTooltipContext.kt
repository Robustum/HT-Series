package io.github.hiiragi283.api.material

import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.material.property.HTTooltipProperty
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText

data class HTMaterialTooltipContext(
    val materialKey: HTMaterialKey,
    val material: HTPropertyHolder,
    val materialTranslatable: HTMaterialTranslatable? = null,
    val stack: ItemStack = ItemStack.EMPTY,
) {
    fun createTooltips(): MutableList<Text> = appendTooltips(mutableListOf())

    fun appendTooltips(lines: MutableList<Text>): MutableList<Text> {
        // Title
        lines.add(TranslatableText("tooltip.ht_materials.material.title"))
        // Name
        val name: String = materialTranslatable?.getTranslatedName(materialKey) ?: materialKey.translatedName
        lines.add(TranslatableText("tooltip.ht_materials.material.name", name))
        // HTShapeType
        // lines.add(TranslatableText("tooltip.ht_materials.material.type", material.type.translatedName))
        // Formula
        material[HTMaterialProperties.FORMULA]?.let { formula: String ->
            lines.add(TranslatableText("tooltip.ht_materials.material.formula", formula))
        }
        // Molar Mass
        material[HTMaterialProperties.MOLAR]?.let { molar: Double ->
            lines.add(TranslatableText("tooltip.ht_materials.material.molar", molar))
        }
        // Tooltip from Properties
        material.forEachProperties { _, property ->
            if (property is HTTooltipProperty) {
                property.appendTooltip(this)
            }
        }
        return lines
    }
}
