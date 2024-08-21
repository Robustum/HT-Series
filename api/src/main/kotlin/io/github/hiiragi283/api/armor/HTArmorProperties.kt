package io.github.hiiragi283.api.armor

import io.github.hiiragi283.api.extension.TypedIdentifier
import io.github.hiiragi283.api.module.HTModuleType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ItemGroup
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Rarity

object HTArmorProperties {
    //    ArmorMaterial    //

    @JvmField
    val DURABILITY: TypedIdentifier<(EquipmentSlot) -> Int> =
        TypedIdentifier.of(HTModuleType.TOOL.id("durability"))

    @JvmField
    val PROTECTION: TypedIdentifier<(EquipmentSlot) -> Int> =
        TypedIdentifier.of(HTModuleType.TOOL.id("protection"))

    @JvmField
    val ENCHANTABILITY: TypedIdentifier<Int> =
        TypedIdentifier.of(HTModuleType.TOOL.id("enchantability"))

    @JvmField
    val EQUIP_SOUND: TypedIdentifier<SoundEvent> =
        TypedIdentifier.of(HTModuleType.TOOL.id("equip_sound"))

    @JvmField
    val REPAIRMENT: TypedIdentifier<Ingredient> =
        TypedIdentifier.of(HTModuleType.TOOL.id("repairment"))

    @JvmField
    val TOUGHNESS: TypedIdentifier<Float> =
        TypedIdentifier.of(HTModuleType.TOOL.id("toughness"))

    @JvmField
    val KNOCKBACK_RESISTANCE: TypedIdentifier<Float> =
        TypedIdentifier.of(HTModuleType.TOOL.id("knockback_resistance"))

    //    Settings    //

    @JvmField
    val ITEM_GROUP: TypedIdentifier<ItemGroup> =
        TypedIdentifier.of(HTModuleType.TOOL.id("item_group"))

    @JvmField
    val RARITY: TypedIdentifier<Rarity> =
        TypedIdentifier.of(HTModuleType.TOOL.id("rarity"))

    @JvmField
    val FIREPROOF: TypedIdentifier<Unit> =
        TypedIdentifier.of(HTModuleType.TOOL.id("fireproof"))
}
