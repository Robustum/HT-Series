package io.github.hiiragi283.api.event

import io.github.hiiragi283.api.event.HTMaterialEvent.ModifyBuilder
import io.github.hiiragi283.api.material.HTMaterialBuilder
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object HTMaterialEvent {
    //    ModifyBuilder    //

    @JvmField
    val MODIFY_BUILDER: Event<ModifyBuilder> = EventFactory.createArrayBacked(ModifyBuilder::class.java) { listeners ->
        ModifyBuilder { builder -> listeners.forEach { it.onBuilding(builder) } }
    }

    fun interface ModifyBuilder {
        fun onBuilding(builder: HTMaterialBuilder)
    }
}
