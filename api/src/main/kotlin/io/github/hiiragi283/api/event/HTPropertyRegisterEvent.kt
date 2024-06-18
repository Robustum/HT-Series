package io.github.hiiragi283.api.event

import io.github.hiiragi283.api.extension.TypedIdentifier
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

fun interface HTPropertyRegisterEvent {
    fun register(handler: Handler)

    companion object {
        @JvmField
        val ITEM: Event<HTPropertyRegisterEvent> = EventFactory.createArrayBacked(HTPropertyRegisterEvent::class.java) { listeners ->
            HTPropertyRegisterEvent { handler -> listeners.forEach { event -> event.register(handler) } }
        }
    }

    class Handler(private val propertyMap: MutableMap<TypedIdentifier<*>, Any>) {
        fun <T : Any> put(id: TypedIdentifier<T>, value: T) {
            propertyMap[id] = value
        }

        fun <T : Any> putOrRemove(id: TypedIdentifier<T>, value: T?) {
            value?.let { put(id, it) } ?: remove(id)
        }

        fun remove(id: TypedIdentifier<*>) {
            propertyMap.remove(id)
        }
    }
}
