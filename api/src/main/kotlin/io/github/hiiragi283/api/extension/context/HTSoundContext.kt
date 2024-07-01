package io.github.hiiragi283.api.extension.context

import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

data class HTSoundContext(
    val soundEvent: SoundEvent,
    val soundCategory: SoundCategory,
    val volume: Float,
    val pitch: Float,
) {
    fun playSound(world: World, pos: BlockPos) {
        world.playSound(null, pos, soundEvent, soundCategory, volume, pitch)
    }
}
