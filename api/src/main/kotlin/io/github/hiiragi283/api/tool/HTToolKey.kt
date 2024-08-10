package io.github.hiiragi283.api.tool

import com.mojang.serialization.Codec
import java.util.concurrent.ConcurrentHashMap

class HTToolKey private constructor(val name: String) {
    companion object {
        private val INSTANCES: MutableMap<String, HTToolKey> = ConcurrentHashMap()

        @JvmStatic
        fun of(name: String): HTToolKey = INSTANCES.computeIfAbsent(name, ::HTToolKey)

        @JvmField
        val CODEC: Codec<HTToolKey> = Codec.STRING.xmap(Companion::of, HTToolKey::name)
    }

    //    Any    //

    override fun equals(other: Any?): Boolean = (other as? HTToolKey)?.name == this.name

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = name
}
