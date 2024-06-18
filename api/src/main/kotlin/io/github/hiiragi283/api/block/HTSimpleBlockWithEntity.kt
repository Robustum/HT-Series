package io.github.hiiragi283.api.block

import io.github.hiiragi283.api.block.entity.HTBlockEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView

open class HTSimpleBlockWithEntity(
    settings: Settings,
    val blockEntityConstructor: (BlockView) -> HTBlockEntity?,
) : HTBlockWithEntity(settings) {
    constructor(settings: Settings, blockEntityConstructor: () -> HTBlockEntity?) : this(
        settings,
        { _ -> blockEntityConstructor() },
    )

    override fun createBlockEntity(world: BlockView): BlockEntity? = blockEntityConstructor(world)
}
