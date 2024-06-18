package io.github.hiiragi283.api.material.property

import io.github.hiiragi283.api.extension.lowerName
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.resource.HTModelJsonBuilder
import net.minecraft.block.Block
import java.util.function.Consumer

abstract class HTItemModelProperty(val shapeKey: HTShapeKey) : Consumer<HTModelJsonBuilder> {
    override fun accept(builder: HTModelJsonBuilder) {
        HTModelJsonBuilder.simpleItemModel(
            builder,
            HTModuleType.API.id(getTextureId()),
            getOverlayTexture()?.let(HTModuleType.API::id),
        )
    }

    open fun getTextureId(): String = "item/${shapeKey.name}"

    open fun getOverlayTexture(): String? = null

    companion object {
        @JvmStatic
        fun ofSimple(shapeKey: HTShapeKey): HTItemModelProperty = Simple(shapeKey)

        @JvmStatic
        fun ofGem(type: HTMaterialGemType, shapeKey: HTShapeKey): HTItemModelProperty = Gem(type, shapeKey)

        @JvmStatic
        fun ofMetal(isShiny: Boolean, shapeKey: HTShapeKey): HTItemModelProperty = Metal(isShiny, shapeKey)

        @JvmStatic
        fun ofBlock(block: Block, shapeKey: HTShapeKey): HTItemModelProperty = BlockItem(block, shapeKey)
    }

    private class Simple(shapeKey: HTShapeKey) : HTItemModelProperty(shapeKey)

    private class Gem(val type: HTMaterialGemType, shapeKey: HTShapeKey) : HTItemModelProperty(shapeKey) {
        override fun getTextureId(): String = if (shapeKey == HTShapeKeys.GEM) "item/gem_${type.lowerName}" else super.getTextureId()

        override fun getOverlayTexture(): String? = "${getTextureId()}_overlay".takeIf { type.hasOverlay }
    }

    private class Metal(val isShiny: Boolean, shapeKey: HTShapeKey) : HTItemModelProperty(shapeKey) {
        override fun getOverlayTexture(): String? = "${getTextureId()}_overlay".takeIf { isShiny }
    }

    private class BlockItem(val block: Block, shapeKey: HTShapeKey) : HTItemModelProperty(shapeKey) {
        override fun accept(builder: HTModelJsonBuilder) {
            HTModelJsonBuilder.blockParented(builder, block)
        }
    }
}
