package io.github.hiiragi283.api.item

import io.github.hiiragi283.api.extension.TypedIdentifier
import io.github.hiiragi283.api.property.HTItemProperties
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler
import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.FoodComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Rarity

class HTItemSettings : FabricItemSettings(), HTPropertyHolder.Mutable {
    override fun equipmentSlot(equipmentSlotProvider: EquipmentSlotProvider): HTItemSettings {
        super.equipmentSlot(equipmentSlotProvider)
        return this
    }

    override fun customDamage(handler: CustomDamageHandler): HTItemSettings {
        super.customDamage(handler)
        return this
    }

    fun <T : Any> addProperty(id: TypedIdentifier<T>, value: T): HTItemSettings = apply {
        set(id, value)
    }

    fun removeProperty(id: TypedIdentifier<*>): HTItemSettings = apply {
        remove(id)
    }

    override fun food(foodComponent: FoodComponent): HTItemSettings = addProperty(HTItemProperties.FOOD_COMPONENT, foodComponent)

    override fun maxCount(maxCount: Int): HTItemSettings = addProperty(HTItemProperties.MAX_COUNT, maxCount)

    override fun maxDamageIfAbsent(maxDamage: Int): HTItemSettings {
        if (getOrDefault(HTItemProperties.MAX_DAMAGE, 0) == 0) {
            maxDamage(maxDamage)
        }
        return this
    }

    override fun maxDamage(maxDamage: Int): HTItemSettings = addProperty(HTItemProperties.MAX_DAMAGE, maxDamage)

    override fun recipeRemainder(recipeRemainder: Item): HTItemSettings = addProperty(HTItemProperties.RECIPE_REMAINDER, recipeRemainder)

    override fun group(group: ItemGroup): HTItemSettings = addProperty(HTItemProperties.GROUP, group)

    override fun rarity(rarity: Rarity): HTItemSettings = addProperty(HTItemProperties.RARITY, rarity)

    override fun fireproof(): HTItemSettings = addProperty(HTItemProperties.FIREPROOF, true)

    //    HTPropertyHolder    //

    val propertyMap: Map<TypedIdentifier<*>, Any>
        get() = propertyMap1
    private val propertyMap1: MutableMap<TypedIdentifier<*>, Any> = hashMapOf()

    override fun <T : Any> get(id: TypedIdentifier<T>): T? = id.cast(propertyMap1[id])

    override fun contains(id: TypedIdentifier<*>): Boolean = id in propertyMap1

    override fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit) {
        propertyMap.forEach(action)
    }

    override fun <T : Any> set(id: TypedIdentifier<T>, value: T) {
        propertyMap1[id] = value
    }

    override fun remove(id: TypedIdentifier<*>) {
        propertyMap1.remove(id)
    }
}
