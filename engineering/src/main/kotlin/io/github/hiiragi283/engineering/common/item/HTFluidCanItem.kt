package io.github.hiiragi283.engineering.common.item

import io.github.hiiragi283.api.extension.buildNbt
import io.github.hiiragi283.api.extension.decodeResult
import io.github.hiiragi283.api.extension.encodeResult
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.fluid.phase.HTPhasedMaterial
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtOps
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList

object HTFluidCanItem : Item(FabricItemSettings().group(ItemGroup.TOOLS)) {
    fun getEmptyCan(): ItemStack = ItemStack(this)

    fun getFilledCan(fluid: Fluid): ItemStack = HTApiHolder.Material
        .apiInstance
        .materialFluidManager[fluid]
        ?.let(::getFilledCan)
        ?: getEmptyCan()

    fun getFilledCan(materialKey: HTMaterialKey, phase: HTFluidPhase): ItemStack = getFilledCan(HTPhasedMaterial(materialKey, phase))

    fun getFilledCan(phasedMaterial: HTPhasedMaterial): ItemStack = getEmptyCan().apply {
        tag = buildNbt {
            put(
                HTModuleType.ENGINEERING.modName,
                HTPhasedMaterial.CODEC
                    .encodeResult(NbtOps.INSTANCE, phasedMaterial)
                    .getOrThrow(),
            )
        }
    }

    fun getPhasedMaterial(stack: ItemStack): HTPhasedMaterial? {
        if (stack.isEmpty) return null
        return stack.tag
            ?.getCompound(HTModuleType.ENGINEERING.modName)
            ?.takeUnless { it.isEmpty }
            ?.let { HTPhasedMaterial.CODEC.decodeResult(NbtOps.INSTANCE, it) }
            ?.getOrNull()
    }

    override fun appendStacks(group: ItemGroup, stacks: DefaultedList<ItemStack>) {
        if (!isIn(group)) return
        stacks.add(getEmptyCan())
        HTFluidPhase.entries.forEach { phase ->
            HTApiHolder.Material
                .apiInstance
                .materialRegistry
                .forEach { key: HTMaterialKey, material: HTPropertyHolder ->
                    if (phase.canGenerateFluid(key, material)) {
                        stacks.add(getFilledCan(key, phase))
                    }
                }
        }
    }

    override fun getName(stack: ItemStack): Text =
        getPhasedMaterial(stack)?.let { it.phase.getTranslatedText(it.materialKey) } ?: super.getName(stack)
}
