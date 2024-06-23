package io.github.hiiragi283.api.item.shape

import io.github.hiiragi283.api.material.HTMaterialIdProvider
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialTagProvider
import io.github.hiiragi283.api.property.HTPropertyHolder

interface HTShape : HTMaterialIdProvider, HTMaterialTagProvider {
    fun canGenerateBlock(materialKey: HTMaterialKey, material: HTPropertyHolder): Boolean

    fun canGenerateItem(materialKey: HTMaterialKey, material: HTPropertyHolder): Boolean
}
