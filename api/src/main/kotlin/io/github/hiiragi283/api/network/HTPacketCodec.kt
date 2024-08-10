package io.github.hiiragi283.api.network

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier

interface HTPacketCodec<T : Any> {
    val id: Identifier

    fun encode(input: T, buf: PacketByteBuf)

    fun decode(buf: PacketByteBuf): T

    fun create(input: T): PacketByteBuf = PacketByteBufs.create().apply { encode(input, this) }

    companion object {
        @JvmStatic
        fun <T : Any> createSimple(id: Identifier, encoder: (T, PacketByteBuf) -> Unit, decoder: (PacketByteBuf) -> T): HTPacketCodec<T> =
            object : HTPacketCodec<T> {
                override val id: Identifier = id

                override fun encode(input: T, buf: PacketByteBuf): Unit = encoder(input, buf)

                override fun decode(buf: PacketByteBuf): T = decoder(buf)
            }
    }
}
