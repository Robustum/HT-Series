package io.github.hiiragi283.api.screen

import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.network.HTPacketCodec

data class HTPropertySync(val syncId: Int, val index: Int, val value: Int) {
    companion object {
        @JvmField
        val PACKET_CODEC: HTPacketCodec<HTPropertySync> = HTPacketCodec.createSimple(
            HTModuleType.API.id("property_delegate_sync"),
            { buf, sync ->
                buf.writeVarInt(sync.syncId)
                buf.writeVarInt(sync.index)
                buf.writeVarInt(sync.value)
            },
            { buf -> HTPropertySync(buf.readVarInt(), buf.readVarInt(), buf.readVarInt()) },
        )
    }
}
