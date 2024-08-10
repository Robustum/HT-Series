package io.github.hiiragi283.api.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.item.ItemStack

object HTItemEvent {

    @JvmField
    val ENCHANTABILITY: Event<CalculateEnchantability> = EventFactory.createArrayBacked(CalculateEnchantability::class.java) { listeners -> 
        CalculateEnchantability { stack -> 
            var ret: Int = stack.item.enchantability
            listeners.forEach { event ->
                event.getEnchantability(stack)?.let { ret = it }
            }
            ret
        }
    }
    
    //    CalculateEnchantability    //
    
    fun interface CalculateEnchantability {
        fun getEnchantability(stack: ItemStack): Int?
    }

}