package io.github.hiiragi283.api.item.shape

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import io.github.hiiragi283.api.extension.RegistryObject
import io.github.hiiragi283.api.extension.getRegistryObject
import io.github.hiiragi283.api.extension.nonAir
import io.github.hiiragi283.api.extension.tryGetValues
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.module.HTModuleType
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.tag.Tag
import net.minecraft.util.registry.Registry

class HTMaterialItemManager(
    private val itemToShaped: Map<Item, HTShapedMaterial>,
    private val shapedToItems: Table<HTMaterialKey, HTShapeKey, MutableSet<Item>>,
    private val unificationBlacklist: Set<Item>,
) {
    companion object {
        @JvmField
        val EMPTY = HTMaterialItemManager(emptyMap(), HashBasedTable.create(), emptySet())
    }

    //    Item -> HTShapedMaterial    //

    operator fun get(stack: ItemStack): HTShapedMaterial? = get(stack.item)

    operator fun get(itemConvertible: ItemConvertible): HTShapedMaterial? = itemToShaped[itemConvertible.asItem()]

    operator fun contains(itemConvertible: ItemConvertible): Boolean = itemConvertible.asItem() in itemToShaped

    fun forEach(action: (Item, HTShapedMaterial) -> Unit) {
        itemToShaped.forEach(action)
    }

    //    HTShapedMaterial -> Fluid    //

    fun convert(stack: ItemStack): ItemStack = convert(stack.item)?.let { ItemStack(it, stack.count) } ?: stack

    fun convert(itemConvertible: ItemConvertible): Item? = itemConvertible
        .asItem()
        ?.takeUnless(unificationBlacklist::contains)
        ?.let(::get)
        ?.let(::getOrNull)

    fun convertibleItems(itemConvertible: ItemConvertible): Set<Item> = itemConvertible
        .asItem()
        ?.takeUnless(unificationBlacklist::contains)
        ?.let(::get)
        ?.let(::get)
        ?: emptySet()

    fun getOrEmpty(shapedMaterial: HTShapedMaterial): Item = getOrEmpty(shapedMaterial.materialKey, shapedMaterial.shapeKey)

    fun getOrEmpty(materialKey: HTMaterialKey, shapeKey: HTShapeKey): Item = getOrNull(materialKey, shapeKey) ?: Items.AIR

    fun getOrNull(shapedMaterial: HTShapedMaterial): Item? = getOrNull(shapedMaterial.materialKey, shapedMaterial.shapeKey)

    fun getOrNull(materialKey: HTMaterialKey, shapeKey: HTShapeKey): Item? {
        val fluids: List<RegistryObject<Item>> = shapedToItems[materialKey, shapeKey]
            ?.mapNotNull(Registry.ITEM::getRegistryObject)
            ?: return null
        val first: RegistryObject<Item> = fluids.firstOrNull { it.id.namespace == "minecraft" }
            ?: fluids.firstOrNull { it.id.namespace == HTModuleType.MATERIAL.modId }
            ?: fluids.getOrNull(0)
            ?: return null
        return first.entry
    }

    //    HTShapedMaterial -> Set<Fluid>    //

    operator fun get(shapedMaterial: HTShapedMaterial): Set<Item> = get(shapedMaterial.materialKey, shapedMaterial.shapeKey)

    operator fun get(materialKey: HTMaterialKey, shapeKey: HTShapeKey): Set<Item> = shapedToItems.get(materialKey, shapeKey) ?: emptySet()

    operator fun contains(shapedMaterial: HTShapedMaterial): Boolean = contains(shapedMaterial.materialKey, shapedMaterial.shapeKey)

    fun contains(materialKey: HTMaterialKey, shapeKey: HTShapeKey): Boolean = shapedToItems.contains(materialKey, shapeKey)

    //    Builder    //

    class Builder(private val map: MutableMap<Entry, HTShapedMaterial>) {
        @JvmOverloads
        fun add(
            materialKey: HTMaterialKey,
            shapeKey: HTShapeKey,
            itemConvertible: ItemConvertible?,
            unification: Boolean = true,
        ) {
            add(HTShapedMaterial(materialKey, shapeKey), itemConvertible?.asItem(), unification)
        }

        private fun add(part: HTShapedMaterial, item: Item?, unification: Boolean = true) {
            part.checkValidation()
            item?.nonAir?.run {
                map[Entry.ItemEntry(item, unification)] = part
            }
        }

        @JvmOverloads
        fun add(
            materialKey: HTMaterialKey,
            shapeKey: HTShapeKey,
            tag: Tag<Item>,
            unification: Boolean = true,
        ) {
            add(HTShapedMaterial(materialKey, shapeKey), tag, unification)
        }

        private fun add(part: HTShapedMaterial, tag: Tag<Item>, unification: Boolean = true) {
            part.checkValidation()
            map[Entry.TagEntry(tag, unification)] = part
        }
    }

    sealed interface Entry {
        val unification: Boolean
        val values: List<Item>

        data class ItemEntry(val item: Item, override val unification: Boolean) : Entry {
            override val values: List<Item> = listOf(item)
        }

        data class TagEntry(val tag: Tag<Item>, override val unification: Boolean) : Entry {
            override val values: List<Item>
                get() = tag.tryGetValues()
        }
    }
}
