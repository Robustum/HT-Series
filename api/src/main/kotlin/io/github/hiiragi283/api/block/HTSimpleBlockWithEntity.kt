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

    // constructor(settings: Settings, blockEntityType: () -> HTBlockEntityType<*>) : this(settings, blockEntityType()::instantiate)

    override fun createBlockEntity(world: BlockView): BlockEntity? = blockEntityConstructor(world)
}
