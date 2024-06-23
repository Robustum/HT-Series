package io.github.hiiragi283.api.item.shape

import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.minecraft.util.Identifier

class HTShapeBuilder private constructor(
    private val key: HTShapeKey,
    private val idPath: String,
    private val tagPath: String,
    val allowBlockGen: Boolean,
    val allowItemGen: Boolean,
) {
    companion object {
        internal fun createBlock(key: HTShapeKey, idPath: String, tagPath: String): HTShapeBuilder =
            HTShapeBuilder(key, idPath, tagPath, true, false)

        internal fun createItem(key: HTShapeKey, idPath: String, tagPath: String): HTShapeBuilder =
            HTShapeBuilder(key, idPath, tagPath, false, true)
    }

    private val blackList: MutableList<HTMaterialKey> = mutableListOf()

    fun addBlacklist(vararg keys: HTMaterialKey) = apply {
        keys.forEach(blackList::add)
    }

    internal fun build(): HTShape = ShapeImpl(key, idPath, tagPath, blackList, allowBlockGen, allowItemGen)

    private data class ShapeImpl(
        private val key: HTShapeKey,
        private val idPath: String,
        private val tagPath: String,
        private val blacklist: List<HTMaterialKey>,
        private val allowBlockGen: Boolean,
        private val allowItemGen: Boolean,
    ) : HTShape {
        override fun canGenerateBlock(materialKey: HTMaterialKey, material: HTPropertyHolder): Boolean =
            allowBlockGen && materialKey !in blacklist && key in material.getOrDefault(
                HTMaterialProperties.BLOCK_SET,
                emptySet(),
            )

        override fun canGenerateItem(materialKey: HTMaterialKey, material: HTPropertyHolder): Boolean =
            allowItemGen && materialKey !in blacklist && key in material.getOrDefault(
                HTMaterialProperties.ITEM_SET,
                emptySet(),
            )

        //    HTMaterialIdProvider    //

        override fun getId(name: String, namespace: String): Identifier = Identifier(namespace, idPath.replace("%s", name))

        //    HTMaterialTagProvider    //

        override fun getTagId(name: String): Identifier = Identifier("c", tagPath.replace("%s", name))
    }
}
