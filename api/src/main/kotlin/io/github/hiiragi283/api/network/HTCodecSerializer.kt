package io.github.hiiragi283.api.network

import com.mojang.serialization.Codec
import io.github.hiiragi283.api.module.HTModuleType
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.minecraft.util.registry.SimpleRegistry

interface HTCodecSerializer<T : Any> {
    val codec: Codec<T>
    val packetCodec: HTPacketCodec<T>

    companion object {
        @JvmField
        val REGISTRY: SimpleRegistry<HTCodecSerializer<*>> =
            FabricRegistryBuilder.createSimple(HTCodecSerializer::class.java, HTModuleType.API.id("codec_serializer"))
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister()
    }
}
