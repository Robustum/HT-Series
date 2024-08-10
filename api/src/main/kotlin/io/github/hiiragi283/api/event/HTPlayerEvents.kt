package io.github.hiiragi283.api.event

import io.github.hiiragi283.api.event.HTPlayerEvents.Pickup
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

object HTPlayerEvents {

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

    @JvmField
    val START_TICK: Event<StartTick> = EventFactory.createArrayBacked(StartTick::class.java) { listeners ->
        StartTick { player -> listeners.forEach { it.onStartTick(player) } }
    }
    
    @JvmField
    val END_TICK: Event<EndTick> = EventFactory.createArrayBacked(EndTick::class.java) { listeners ->
        EndTick { player -> listeners.forEach { it.onEndTick(player) } }
    }
    
    //    Pickup    //
    
    fun interface Pickup {
        fun onPickup(stack: ItemStack): ItemStack
    }
    
    //    StartTick    //
    
    fun interface StartTick {
        fun onStartTick(player: PlayerEntity)
    }
    
    //    EndTick    //

    fun interface EndTick {
        fun onEndTick(player: PlayerEntity)
    }
}