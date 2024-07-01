package io.github.hiiragi283.api.fluid.phase

import com.mojang.serialization.Codec
import io.github.hiiragi283.api.extension.enumCodec
import io.github.hiiragi283.api.material.HTMaterialIdProvider
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialTagProvider
import io.github.hiiragi283.api.material.HTMaterialTranslatable
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.fabricmc.fabric.api.tag.TagRegistry
import net.minecraft.fluid.Fluid
import net.minecraft.tag.Tag
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey

enum class HTFluidPhase(
    private val idPath: String,
    private val tagPath: String,
    val textureId: Identifier,
) : HTMaterialIdProvider, HTMaterialTagProvider, HTMaterialTranslatable {
    POWDER("%s_powder", "%s_powders", Identifier("block/white_concrete_powder")),
    LIQUID("%s", "%s", Identifier("block/mushroom_stem")),
    GAS("%s_gas", "%s_gases", Identifier("block/white_concrete")),
    PLASMA("%s_plasma", "%s_plasmas", Identifier("block/white_concrete")),
    ;

    companion object {
        @JvmField
        val CODEC: Codec<HTFluidPhase> = enumCodec(HTFluidPhase::valueOf)
    }

    private val blacklist: MutableList<HTMaterialKey> = mutableListOf()

    fun addBlacklist(vararg keys: HTMaterialKey) = apply {
        keys.forEach(blacklist::add)
    }

    fun canGenerateFluid(materialKey: HTMaterialKey, material: HTPropertyHolder): Boolean =
        materialKey !in blacklist && this in material.getOrDefault(HTMaterialProperties.FLUID_SET, emptySet())

    //    HTMaterialIdProvider    //

    override fun getId(name: String, namespace: String): Identifier = Identifier(namespace, idPath.replace("%s", name))

    fun getFluidRegistryKey(materialKey: HTMaterialKey, namespace: String = HTModuleType.MATERIAL.modId): RegistryKey<Fluid> =
        getRegistryKey(Registry.FLUID_KEY, materialKey, namespace)

    fun getFluidRegistryKey(name: String, namespace: String = HTModuleType.MATERIAL.modId): RegistryKey<Fluid> =
        getRegistryKey(Registry.FLUID_KEY, name, namespace)

    //    HTMaterialTagProvider    //

    override fun getTagId(name: String) = Identifier("c", tagPath.replace("%s", name))

    fun getFluidTag(materialKey: HTMaterialKey): Tag<Fluid> = getFluidTag(materialKey.name)

    fun getFluidTag(name: String): Tag<Fluid> = getTag(name, TagRegistry::fluid)

    //    HTMaterialTranslatable    //

    override val translationKey: String = "ht_shape.$name"
}
