package io.github.hiiragi283.api.property

import io.github.hiiragi283.api.extension.TypedIdentifier
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.Rarity

object HTItemProperties {
    @JvmField
    val GROUP: TypedIdentifier<ItemGroup> = TypedIdentifier.of(Identifier("item_group"))

    @JvmField
    val RARITY: TypedIdentifier<Rarity> = TypedIdentifier.of(Identifier("rarity"))

    @JvmField
    val MAX_COUNT: TypedIdentifier<Int> = TypedIdentifier.of(Identifier("max_count"))

    @JvmField
    val MAX_DAMAGE: TypedIdentifier<Int> = TypedIdentifier.of(Identifier("max_damage"))

    @JvmField
    val FIREPROOF: TypedIdentifier<Boolean> = TypedIdentifier.of(Identifier("fireproof"))

    @JvmField
    val RECIPE_REMAINDER: TypedIdentifier<Item> = TypedIdentifier.of(Identifier("recipe_remainder"))

    @JvmField
    val FOOD_COMPONENT: TypedIdentifier<FoodComponent> = TypedIdentifier.of(Identifier("food_component"))
}
