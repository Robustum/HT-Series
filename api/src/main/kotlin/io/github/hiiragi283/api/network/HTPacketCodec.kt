package io.github.hiiragi283.api.network

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

interface HTPacketCodec<T : Any> {
    val id: Identifier

    fun encode(buf: PacketByteBuf, input: T)

    fun decode(buf: PacketByteBuf): T

    fun create(input: T): PacketByteBuf = PacketByteBufs.create().apply { encode(this, input) }

    companion object {
        @JvmStatic
        fun <T : Any> createSimple(id: Identifier, encoder: (PacketByteBuf, T) -> Unit, decoder: (PacketByteBuf) -> T): HTPacketCodec<T> =
            object : HTPacketCodec<T> {
                override val id: Identifier = id

                override fun encode(buf: PacketByteBuf, input: T): Unit = encoder(buf, input)

                override fun decode(buf: PacketByteBuf): T = decoder(buf)
            }
    }
}
