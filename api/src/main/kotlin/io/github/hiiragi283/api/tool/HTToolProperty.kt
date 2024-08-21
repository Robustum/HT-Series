package io.github.hiiragi283.api.tool

import io.github.hiiragi283.api.property.HTPropertyHolder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient

class HTToolProperty(holder: HTPropertyHolder) : ToolMaterial, HTPropertyHolder by holder {
    val settings: FabricItemSettings = FabricItemSettings().apply {
        ifPresent(HTToolProperties.ITEM_GROUP, ::group)
        ifPresent(HTToolProperties.RARITY, ::rarity)
        ifPresent(HTToolProperties.FIREPROOF) { fireproof() }
    }

    //    ToolMaterial    //

    override fun getDurability(): Int = getOrDefault(HTToolProperties.DURABILITY, 0)

    override fun getMiningSpeedMultiplier(): Float = getOrDefault(HTToolProperties.MINING_SPEED, 0.0f)

    override fun getAttackDamage(): Float = getOrDefault(HTToolProperties.ATTACK_DAMAGE, 0f)

    override fun getMiningLevel(): Int = getOrDefault(HTToolProperties.MINING_LEVEL, 0)

    override fun getEnchantability(): Int = getOrDefault(HTToolProperties.ENCHANTABILITY, 0)

    override fun getRepairIngredient(): Ingredient = getOrDefault(HTToolProperties.REPAIRMENT, Ingredient.EMPTY)
}
