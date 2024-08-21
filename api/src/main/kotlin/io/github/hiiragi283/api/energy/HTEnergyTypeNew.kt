package io.github.hiiragi283.api.energy

import io.github.hiiragi283.api.extension.buildNbt
import io.github.hiiragi283.api.extension.lowerName
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf

@Suppress("UnstableApiUsage")
enum class HTEnergyTypeNew : TransferVariant<HTEnergyTypeNew> {
    EMPTY,
    COOLANT,
    ELECTRICITY,
    ENCHANTMENT,
    HEAT,
    SOUL,
    ;

    override fun isBlank(): Boolean = this == EMPTY

    override fun getObject(): HTEnergyTypeNew = this

    override fun getNbt(): NbtCompound? = null

    override fun toNbt(): NbtCompound = buildNbt {
        putString("type", lowerName)
    }

    override fun toPacket(buf: PacketByteBuf) {
        buf.writeString(lowerName)
    }
}
