package io.github.hiiragi283.api.armor

import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents

class HTArmorProperty(
    val materialKey: HTMaterialKey,
    holder: HTPropertyHolder,
) : HTPropertyHolder by holder, ArmorMaterial {
    val settings: FabricItemSettings = FabricItemSettings().apply {
        ifPresent(HTArmorProperties.ITEM_GROUP, ::group)
        ifPresent(HTArmorProperties.RARITY, ::rarity)
        ifPresent(HTArmorProperties.FIREPROOF) { fireproof() }
    }

    //    ArmorMaterial    //

    override fun getDurability(slot: EquipmentSlot): Int = get(HTArmorProperties.DURABILITY)?.invoke(slot) ?: 0

    override fun getProtectionAmount(slot: EquipmentSlot): Int = get(HTArmorProperties.PROTECTION)?.invoke(slot) ?: 0

    override fun getEnchantability(): Int = getOrDefault(HTArmorProperties.ENCHANTABILITY, 0)

    override fun getEquipSound(): SoundEvent = getOrDefault(HTArmorProperties.EQUIP_SOUND, SoundEvents.ITEM_ARMOR_EQUIP_IRON)

    override fun getRepairIngredient(): Ingredient = getOrDefault(HTArmorProperties.REPAIRMENT, Ingredient.EMPTY)

    override fun getName(): String = materialKey.name

    override fun getToughness(): Float = getOrDefault(HTArmorProperties.TOUGHNESS, 0f)

    override fun getKnockbackResistance(): Float = getOrDefault(HTArmorProperties.KNOCKBACK_RESISTANCE, 0f)
}
