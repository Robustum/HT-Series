package io.github.hiiragi283.api.armor

import io.github.hiiragi283.api.material.property.HTMaterialProperties
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.DyeableArmorItem
import net.minecraft.item.ItemStack

class HTArmorItem(
    val armorProperty: HTArmorProperty,
    slot: EquipmentSlot,
) : DyeableArmorItem(armorProperty, slot, armorProperty.settings) {
    //    DyeableItem    //

    override fun hasColor(stack: ItemStack): Boolean = false

    override fun getColor(stack: ItemStack): Int = armorProperty.materialKey
        .get()
        .get(HTMaterialProperties.COLOR)
        ?.rgb
        ?: -1

    override fun removeColor(stack: ItemStack) {}

    override fun setColor(stack: ItemStack, color: Int) {}
}
