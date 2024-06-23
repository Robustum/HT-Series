package io.github.hiiragi283.material.impl

import io.github.hiiragi283.api.fluid.phase.HTMaterialFluidManager
import io.github.hiiragi283.api.item.shape.HTMaterialItemManager
import io.github.hiiragi283.api.item.shape.HTShapeRegistry
import io.github.hiiragi283.api.material.HTMaterialRegistry
import io.github.hiiragi283.api.material.content.HTMaterialContentManager
import io.github.hiiragi283.api.module.HTMaterialsAPI
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

internal object HTMaterialsAPIImpl : HTMaterialsAPI {
    override lateinit var itemGroup: ItemGroup
        internal set
    override var shapeRegistry: HTShapeRegistry = HTShapeRegistry.build { }
    override var materialRegistry: HTMaterialRegistry = HTMaterialRegistry.build { }

    override var materialContentManager: HTMaterialContentManager = HTMaterialContentManager.EMPTY
    override var materialFluidManager: HTMaterialFluidManager = HTMaterialFluidManager.EMPTY
    override var materialItemManager: HTMaterialItemManager = HTMaterialItemManager.EMPTY

    lateinit var iconItem: Item
    lateinit var dictionaryItem: Item
    lateinit var libraryBlock: Block
}
