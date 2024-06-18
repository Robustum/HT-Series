package io.github.hiiragi283.api.material

import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.module.HTModuleType
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Supplier

class HTMaterialKey private constructor(val name: String) : Supplier<HTMaterial>, HTMaterialKeyable {
    companion object {
        private val INSTANCES: MutableMap<String, HTMaterialKey> = ConcurrentHashMap()

        @JvmStatic
        fun of(name: String): HTMaterialKey = INSTANCES.computeIfAbsent(name, ::HTMaterialKey)
    }

    //    Supplier    //

    var validated: Boolean = false
        internal set(value) {
            if (validated) return
            HTLogger.log { it.info("Material; $name validated!") }
            field = value
        }

    fun checkValidation(): HTMaterialKey = apply {
        check(validated) { "Material; $name is not registered!" }
    }

    private var cache: HTMaterial? = null

    override fun get(): HTMaterial {
        checkValidation()
        if (cache == null) {
            cache = HTApiHolder.Material.apiInstance.materialRegistry[this]
        }
        return checkNotNull(cache) { "Material; $name is not registered!" }
    }

    //    HTMaterialKeyable    //

    override val materialKey: HTMaterialKey = this

    //    Identifier    //

    fun getId(namespace: String = HTModuleType.MATERIAL.modId): Identifier = Identifier(namespace, name)

    val commonId: Identifier
        get() = Identifier("c", name)

    //    Translation    //

    val translationKey: String = "ht_material.$name"

    val translatedName: String
        get() = translatedText.string

    val translatedText: TranslatableText
        get() = TranslatableText(translationKey)

    //    Any    //

    override fun equals(other: Any?): Boolean = (other as? HTMaterialKey)?.name == this.name

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = name
}
