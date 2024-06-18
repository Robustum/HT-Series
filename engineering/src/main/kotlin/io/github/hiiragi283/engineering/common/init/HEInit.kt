package io.github.hiiragi283.engineering.common.init

import io.github.hiiragi283.api.module.HTModuleType
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType

object HEInit {
    init {
        HEBlocks
        HEItems
        HEBlockEntityTypes
        HERecipeTypes
        HEMultiblockShapes
    }

    //    ScreenHandlerType    //

    private fun <T : ScreenHandler> registerScreenHandler(
        path: String,
        builder: ScreenHandlerRegistry.SimpleClientHandlerFactory<T>,
    ): ScreenHandlerType<T> = ScreenHandlerRegistry.registerSimple(HTModuleType.ENGINEERING.id(path), builder)
}
