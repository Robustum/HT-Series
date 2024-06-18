package io.github.hiiragi283.api.fluid

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.fluid.Fluid
import net.minecraft.fluid.Fluids
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.registry.Registry
import java.util.*
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

class HTFluidStack(fluid: Fluid, amount: Long = 1000, nbt: NbtCompound? = null) {
    constructor(fluid: Fluid, amount: Long, nbt: Optional<NbtCompound>) : this(fluid, amount, nbt.getOrNull())

    private val fluid: Fluid = fluid
        get() = if (isEmpty) Fluids.EMPTY else field

    private var amount: Long = amount
        get() = if (isEmpty) 0 else field

    var nbt: NbtCompound? = nbt
        private set

    private val optionalNbt: Optional<NbtCompound>
        get() = Optional.ofNullable(nbt)

    companion object {
        @JvmField
        val EMPTY = HTFluidStack(Fluids.EMPTY)

        @JvmField
        val CODEC: Codec<HTFluidStack> = RecordCodecBuilder.create { instance ->
            instance.group(
                Registry.FLUID.fieldOf("id").forGetter(HTFluidStack::fluid),
                Codec.LONG.orElse(1000).fieldOf("amount").forGetter(HTFluidStack::amount),
                NbtCompound.CODEC.optionalFieldOf("tag").forGetter(HTFluidStack::optionalNbt),
            ).apply(instance, ::HTFluidStack)
        }
    }

    fun setAmount(amount: Long) {
        this.amount = amount
    }

    operator fun plusAssign(amount: Long) {
        this.amount += amount
        updateEmptyState()
    }

    operator fun minusAssign(amount: Long) {
        this.amount -= amount
        updateEmptyState()
    }

    var isEmpty: Boolean = false
        get() = this == EMPTY || fluid == Fluids.EMPTY || amount <= 0 || field
        private set

    val isNotEmpty: Boolean
        get() = !isEmpty

    private fun updateEmptyState() {
        isEmpty = false
        isEmpty = isEmpty
    }

    fun split(amount: Long): HTFluidStack {
        val i: Long = min(amount, this.amount)
        this -= i
        return copyThen { setAmount(i) }
    }

    fun copy(): HTFluidStack = if (isEmpty) EMPTY else HTFluidStack(fluid, amount, nbt)

    inline fun copyThen(action: HTFluidStack.() -> Unit): HTFluidStack = copy().apply(action)
}
