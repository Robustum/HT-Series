package io.github.hiiragi283.api.effect

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectType
import java.awt.Color

class HTStatusEffect(type: StatusEffectType, color: Int) : StatusEffect(type, color) {
    constructor(type: StatusEffectType, color: Color) : this(type, color.rgb)
}