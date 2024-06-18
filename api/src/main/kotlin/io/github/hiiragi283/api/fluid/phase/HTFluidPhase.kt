package io.github.hiiragi283.api.fluid.phase

import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialKeyable
import io.github.hiiragi283.api.material.HTMaterialTagProvider
import io.github.hiiragi283.api.material.HTMaterialTranslatable
import io.github.hiiragi283.api.module.HTModuleType
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
) : HTMaterialTranslatable, HTMaterialTagProvider {
    POWDER("%s_powder", "%s_powders", Identifier("block/white_concrete_powder")),
    LIQUID("%s", "%s", Identifier("block/mushroom_stem")),
    GAS("%s_gas", "%s_gases", Identifier("block/white_concrete")),
    PLASMA("%s_plasma", "%s_plasmas", Identifier("block/white_concrete")),
    ;

    //    Identifier    //

    fun getId(keyable: HTMaterialKeyable, namespace: String = HTModuleType.MATERIAL.modId): Identifier =
        getId(keyable.materialKey.name, namespace)

    fun getId(name: String, namespace: String = HTModuleType.MATERIAL.modId): Identifier = Identifier(namespace, idPath.replace("%s", name))

    fun getRegistryKey(keyable: HTMaterialKeyable, namespace: String = HTModuleType.MATERIAL.modId): RegistryKey<Fluid> =
        RegistryKey.of(Registry.FLUID_KEY, getId(keyable, namespace))

    fun getRegistryKey(name: String, namespace: String = HTModuleType.MATERIAL.modId): RegistryKey<Fluid> =
        RegistryKey.of(Registry.FLUID_KEY, getId(name, namespace))

    //    Tag    //

    override fun getTagId(name: String) = Identifier("c", tagPath.replace("%s", name))

    fun getTag(materialKey: HTMaterialKey): Tag<Fluid> = getTag(materialKey.name)

    fun getTag(name: String): Tag<Fluid> = getTag(name, TagRegistry::fluid)

    //    HTMaterialTranslatable    //

    override val translationKey: String = "ht_shape.$name"
}
