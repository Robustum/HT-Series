package io.github.hiiragi283.api.material.property

import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.HTMaterialKey
import net.minecraft.block.Block

sealed class HTStorageBlockRecipe(val materialKey: HTMaterialKey, val count: Int, val pattern: List<String>) {
    val block: Block?
        get() = materialKey.get().getBlock(HTShapeKeys.BLOCK)

    class Four(materialKey: HTMaterialKey) : HTStorageBlockRecipe(materialKey, 4, listOf("AA", "AA"))

    class Nine(materialKey: HTMaterialKey) : HTStorageBlockRecipe(materialKey, 9, listOf("AAA", "AAA", "AAA"))
}
