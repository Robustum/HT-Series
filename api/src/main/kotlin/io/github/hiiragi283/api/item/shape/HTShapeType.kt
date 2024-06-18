package io.github.hiiragi283.api.item.shape

import net.minecraft.block.Block
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

@Suppress("UNCHECKED_CAST")
class HTShapeType<T : Any> private constructor(val name: String, val registryKey: RegistryKey<Registry<T>>) {
    @Suppress("UNCHECKED_CAST")
    fun cast(obj: Any): T? = obj as? T

    override fun toString(): String = "HTShapeType[name=$name]"

    companion object {
        @JvmField
        val BLOCK: HTShapeType<Block> = HTShapeType("block", Registry.BLOCK_KEY)

        @JvmField
        val FLUID: HTShapeType<Fluid> = HTShapeType("fluid", Registry.FLUID_KEY)

        @JvmField
        val ITEM: HTShapeType<Item> = HTShapeType("item", Registry.ITEM_KEY)

        @JvmStatic
        fun <T : Any> fromClass(obj: Any): HTShapeType<T>? = when (obj) {
            is Block -> BLOCK
            is Item -> ITEM
            else -> null
        } as? HTShapeType<T>

        @JvmStatic
        fun <T : Any> getType(registryKey: RegistryKey<Registry<T>>): HTShapeType<T>? = when (registryKey) {
            Registry.BLOCK_KEY -> BLOCK
            Registry.ITEM_KEY -> ITEM
            else -> null
        } as? HTShapeType<T>
    }
}
