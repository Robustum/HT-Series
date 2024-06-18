package io.github.hiiragi283.api.multiblock.hatch

import io.github.hiiragi283.api.storage.HTStorageIO
import io.github.hiiragi283.api.storage.HTStorageType
import net.minecraft.nbt.NbtCompound

enum class HTHatchType(val entryType: HTStorageType, val ioType: HTStorageIO) {
    ITEM_INPUT(HTStorageType.ITEM, HTStorageIO.INPUT),
    ITEM_OUTPUT(HTStorageType.ITEM, HTStorageIO.OUTPUT),
    FLUID_INPUT(HTStorageType.FLUID, HTStorageIO.INPUT),
    FLUID_OUTPUT(HTStorageType.FLUID, HTStorageIO.OUTPUT),
    ;

    companion object {
        @JvmStatic
        fun from(value: String): HTHatchType = runCatching { valueOf(value) }.getOrDefault(ITEM_INPUT)

        @JvmStatic
        fun fromNbt(nbtCompound: NbtCompound): HTHatchType = nbtCompound.getString("Type").let(this::from)
    }

    fun writeNbt(nbtCompound: NbtCompound) {
        nbtCompound.putString("Type", this.name)
    }

    operator fun component1() = entryType

    operator fun component2() = ioType
}
