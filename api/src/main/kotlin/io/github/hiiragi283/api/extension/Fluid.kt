package io.github.hiiragi283.api.extension

import net.minecraft.fluid.Fluid
import net.minecraft.fluid.Fluids

val Fluid.isAir: Boolean
    get() = this == Fluids.EMPTY

val Fluid.nonAir: Fluid?
    get() = takeUnless { isAir }
