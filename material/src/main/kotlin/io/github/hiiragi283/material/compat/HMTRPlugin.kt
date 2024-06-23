package io.github.hiiragi283.material.compat

import io.github.hiiragi283.api.extension.buildDefaultedList
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.fluid.phase.HTMaterialFluidManager
import io.github.hiiragi283.api.item.shape.HTMaterialItemManager
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.item.shape.HTShapeRegistry
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialKeys
import io.github.hiiragi283.api.module.HTMaterialsAPI
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.module.HTPlugin
import io.github.hiiragi283.api.resource.HTRuntimeDataRegistry
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import reborncore.common.crafting.RebornRecipe
import reborncore.common.crafting.ingredient.DummyIngredient
import reborncore.common.crafting.ingredient.RebornIngredient
import reborncore.common.crafting.ingredient.TagIngredient
import techreborn.init.ModFluids
import techreborn.init.ModRecipes
import techreborn.init.TRContent
import java.util.*

@Suppress("unused")
object HMTRPlugin : HTPlugin.Material {
    override val modId: String = "techreborn"
    override val priority: Int = 0

    @JvmField
    val SMALL_DUST = HTShapeKey.of("small_dust")

    override fun registerShape(builder: HTShapeRegistry.Builder) {
        builder.createItemShape(SMALL_DUST)
    }

    override fun bindMaterialWithFluid(builder: HTMaterialFluidManager.Builder) {
        super.bindMaterialWithFluid(builder)
        builder.add(HTMaterialKeys.BERYLLIUM, HTFluidPhase.LIQUID, ModFluids.BERYLLIUM.fluid)
        builder.add(HTMaterialKeys.CALCIUM, HTFluidPhase.LIQUID, ModFluids.CALCIUM.fluid)

        builder.add(HTMaterialKeys.CARBON, HTFluidPhase.LIQUID, ModFluids.CARBON.fluid)

        builder.add(HTMaterialKeys.HELIUM, HTFluidPhase.GAS, ModFluids.HELIUM.fluid)

        builder.add(HTMaterialKeys.HELIUM, HTFluidPhase.PLASMA, ModFluids.HELIUMPLASMA.fluid)
        builder.add(HTMaterialKeys.HYDROGEN, HTFluidPhase.GAS, ModFluids.HYDROGEN.fluid)
        builder.add(HTMaterialKeys.LITHIUM, HTFluidPhase.LIQUID, ModFluids.LITHIUM.fluid)
        builder.add(HTMaterialKeys.MERCURY, HTFluidPhase.LIQUID, ModFluids.MERCURY.fluid)

        builder.add(HTMaterialKeys.POTASSIUM, HTFluidPhase.LIQUID, ModFluids.POTASSIUM.fluid)
        builder.add(HTMaterialKeys.SILICON, HTFluidPhase.LIQUID, ModFluids.SILICON.fluid)
        builder.add(HTMaterialKeys.SODIUM, HTFluidPhase.LIQUID, ModFluids.SODIUM.fluid)

        builder.add(HTMaterialKeys.SULFUR, HTFluidPhase.LIQUID, ModFluids.SULFUR.fluid)

        builder.add(HTMaterialKeys.TUNGSTEN, HTFluidPhase.LIQUID, ModFluids.WOLFRAMIUM.fluid)
    }

    override fun bindMaterialWithItem(builder: HTMaterialItemManager.Builder) {
        super.bindMaterialWithItem(builder)
        builder.add(HTMaterialKeys.PHOSPHORUS, HTShapeKeys.DUST, TRContent.Dusts.PHOSPHOROUS)
        builder.add(HTMaterialKeys.PHOSPHORUS, SMALL_DUST, TRContent.SmallDusts.PHOSPHOROUS)
        builder.add(HTMaterialKeys.RUBY, HTShapeKeys.GEM, TRContent.Gems.RUBY)
        builder.add(HTMaterialKeys.SAPPHIRE, HTShapeKeys.GEM, TRContent.Gems.SAPPHIRE)
        builder.add(HTMaterialKeys.WOOD, HTShapeKeys.DUST, TRContent.Dusts.SAW)
    }

    override fun afterMaterialRegistration(instance: HTMaterialsAPI, isClient: Boolean) {
        instance.materialRegistry.forEach { key: HTMaterialKey, _ ->
            val dustItem: Item = instance.materialItemManager.getOrNull(key, HTShapeKeys.DUST) ?: return@forEach
            // 1x Chunk -> 2x Dust with Grinder
            HTRuntimeDataRegistry.addRecipe(
                RebornRecipe(
                    ModRecipes.GRINDER,
                    HTModuleType.MATERIAL.id("grinder/$key"),
                    buildDefaultedList<RebornIngredient>(1, DummyIngredient()) {
                        set(
                            0,
                            TagIngredient(
                                HTShapeKeys.RAW_CHUNK.get().getTagId(key),
                                HTShapeKeys.RAW_CHUNK.get().getItemTag(key),
                                Optional.empty(),
                            ),
                        )
                    },
                    buildDefaultedList(1, ItemStack.EMPTY) {
                        set(0, ItemStack(dustItem, 2))
                    },
                    2,
                    200,
                ),
            )
        }
    }
}
