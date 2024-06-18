package io.github.hiiragi283.api.event

import io.github.hiiragi283.api.event.HTMaterialEvent.ModifyBuilder
import io.github.hiiragi283.api.material.HTMaterial
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object HTMaterialEvent {
    /*private fun <T : Any, U : Any> registerContentEvent(): Event<ContentRegister<T, U>> =
        EventFactory.createArrayBacked(ContentRegister::class.java) { listeners ->
            ContentRegister { materialKey, shape, obj, materialBuilder ->
                listeners.forEach { it.onRegistered(materialKey, shape, obj, materialBuilder) }
            }
        }

    @JvmField
    val BLOCK_CONTENT: Event<ContentRegister<Block, HTShape.Simple>> = registerContentEvent()

    @JvmField
    val FLUID_CONTENT: Event<ContentRegister<Fluid, HTFluidPhase>> = registerContentEvent()

    @JvmField
    val ITEM_CONTENT: Event<ContentRegister<Item, HTShape.Simple>> = registerContentEvent()

    //    ContentRegister    //

    fun interface ContentRegister<T : Any, U : Any> {
        fun onRegistered(
            materialKey: HTMaterialKey,
            shape: U,
            obj: T?,
            materialBuilder: HTMaterialRegistry.Builder,
        )
    }*/

    //    ModifyBuilder    //

    @JvmField
    val MODIFY_BUILDER: Event<ModifyBuilder> = EventFactory.createArrayBacked(ModifyBuilder::class.java) { listeners ->
        ModifyBuilder { builder -> listeners.forEach { it.onBuilding(builder) } }
    }

    fun interface ModifyBuilder {
        fun onBuilding(builder: HTMaterial.Builder)
    }
}
