package io.github.hiiragi283.api.event

import io.github.hiiragi283.api.event.HTPlayerEvents.Pickup
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.item.ItemStack

object HTPlayerEvents {
    //    Pickup    //

    @JvmField
    val PICKUP: Event<Pickup> = EventFactory.createArrayBacked(Pickup::class.java) { listeners ->
        Pickup { stack ->
            var result: ItemStack = stack
            listeners.forEach { event ->
                val ret: ItemStack = event.onPickup(stack)
                if (!ret.isEmpty) result = ret
            }
            result
        }
    }

    fun interface Pickup {
        fun onPickup(stack: ItemStack): ItemStack
    }
}
