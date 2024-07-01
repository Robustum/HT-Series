package io.github.hiiragi283.api.multiblock.hatch

import io.github.hiiragi283.api.extension.runCatchAndLog
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.minecraft.nbt.NbtCompound

@Suppress("UnstableApiUsage")
enum class HTHatchTier(val itemSlots: Int, val fluidSlots: Int, val size: Long) {
    BASIC(2, 1, FluidConstants.BUCKET * 4),
    ADVANCED(4, 2, FluidConstants.BUCKET * 16),
    INDUSTRIAL(6, 3, FluidConstants.BUCKET * 64),
    ;

    companion object {
        @JvmStatic
        fun from(value: String): HTHatchTier = runCatchAndLog { HTHatchTier.valueOf(value) }.getOrDefault(BASIC)

        @JvmStatic
        fun fromNbt(nbtCompound: NbtCompound): HTHatchTier = nbtCompound.getString("Tier").let(this::from)
    }

    fun writeNbt(nbtCompound: NbtCompound) {
        nbtCompound.putString("Tier", this.name)
    }
}
