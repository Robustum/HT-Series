package io.github.hiiragi283.api.extension

import io.github.hiiragi283.api.network.HTPacketCodec
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.Direction

//    Network    //

fun <T : Any> sendPlayersFromBE(
    blockEntity: BlockEntity,
    envType: EnvType = EnvType.SERVER,
    codec: HTPacketCodec<T>,
    input: T,
) {
    when (envType) {
        EnvType.CLIENT -> ClientPlayNetworking.send(codec.id, codec.create(input))
        EnvType.SERVER -> PlayerLookup.tracking(blockEntity).forEach { player ->
            sendS2CPacket(codec, input, player)
        }
    }
}

fun <T : Any> sendS2CPacket(codec: HTPacketCodec<T>, input: T, player: ServerPlayerEntity) {
    ServerPlayNetworking.send(player, codec.id, codec.create(input))
}

fun <T : Any> sendC2SPacket(codec: HTPacketCodec<T>, input: T) {
    ClientPlayNetworking.send(codec.id, codec.create(input))
}

fun <T : Any> registerClientReceiver(
    codec: HTPacketCodec<T>,
    action: (MinecraftClient, ClientPlayNetworkHandler, T, PacketSender) -> Unit,
) {
    ClientPlayNetworking.registerGlobalReceiver(codec.id) { client, handler, buf, sender ->
        action(client, handler, codec.decode(buf), sender)
    }
}

fun <T : Any> registerServerReceiver(
    codec: HTPacketCodec<T>,
    action: (MinecraftServer, ServerPlayerEntity, ServerPlayNetworkHandler, T, PacketSender) -> Unit,
) {
    ServerPlayNetworking.registerGlobalReceiver(codec.id) { server, player, handler, buf, sender ->
        action(server, player, handler, codec.decode(buf), sender)
    }
}

//    PacketByteBuf    //

inline fun buildPacket(buildAction: PacketByteBuf.() -> Unit): PacketByteBuf = PacketByteBufs.create().apply(buildAction)

fun PacketByteBuf.writeDirection(direction: Direction) {
    writeVarInt(direction.ordinal)
}

fun PacketByteBuf.readDirection(): Direction = Direction.byId(readVarInt() % 6)
