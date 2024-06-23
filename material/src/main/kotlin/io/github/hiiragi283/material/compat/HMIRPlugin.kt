package io.github.hiiragi283.material.compat

import io.github.hiiragi283.api.extension.HTColor
import io.github.hiiragi283.api.extension.averageColor
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.fluid.phase.HTMaterialFluidManager
import io.github.hiiragi283.api.item.shape.HTMaterialItemManager
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.item.shape.HTShapeRegistry
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialKeys
import io.github.hiiragi283.api.material.HTMaterialRegistry
import io.github.hiiragi283.api.module.HTPlugin
import me.steven.indrev.IndustrialRevolution
import me.steven.indrev.registry.IRFluidRegistry
import me.steven.indrev.registry.IRItemRegistry
import net.minecraft.fluid.Fluid
import net.minecraft.util.registry.Registry

@Suppress("unused")
object HMIRPlugin : HTPlugin.Material {
    override val modId: String = IndustrialRevolution.MOD_ID
    override val priority: Int = 0

    @JvmField
    val CHUNK = HTShapeKey.of("chunk")

    @JvmField
    val PURIFIED_ORE = HTShapeKey.of("purified_ore")

    override fun registerShape(builder: HTShapeRegistry.Builder) {
        builder.createItemShape(CHUNK)
        builder.createItemShape(PURIFIED_ORE)

        builder.getBuilder(HTShapeKeys.DUST)?.addBlacklist(HTMaterialKeys.NIKOLITE)
        builder.getBuilder(HTShapeKeys.INGOT)?.addBlacklist(HTMaterialKeys.NIKOLITE)
    }

    override fun registerMaterial(builder: HTMaterialRegistry.Builder) {
        builder.createMetal(HTMaterialKeys.NIKOLITE, false)
            .color(averageColor(HTColor.DARK_BLUE, HTColor.DARK_GREEN))
    }

    override fun bindMaterialWithFluid(builder: HTMaterialFluidManager.Builder) {
        super.bindMaterialWithFluid(builder)
        mapOf(
            HTMaterialKeys.COPPER to IRFluidRegistry.MOLTEN_COPPER_STILL,
            HTMaterialKeys.GOLD to IRFluidRegistry.MOLTEN_GOLD_STILL,
            HTMaterialKeys.IRON to IRFluidRegistry.MOLTEN_IRON_STILL,
            HTMaterialKeys.LEAD to IRFluidRegistry.MOLTEN_LEAD_STILL,
            HTMaterialKeys.NETHERITE to IRFluidRegistry.MOLTEN_NETHERITE_STILL,
            HTMaterialKeys.SILVER to IRFluidRegistry.MOLTEN_SILVER_STILL,
            HTMaterialKeys.TIN to IRFluidRegistry.MOLTEN_TIN_STILL,
        ).forEach { (key: HTMaterialKey, fluid: Fluid) -> builder.add(key, HTFluidPhase.LIQUID, fluid) }
    }

    override fun bindMaterialWithItem(builder: HTMaterialItemManager.Builder) {
        super.bindMaterialWithItem(builder)
        listOf(
            HTMaterialKeys.COPPER,
            HTMaterialKeys.GOLD,
            HTMaterialKeys.IRON,
            HTMaterialKeys.LEAD,
            HTMaterialKeys.NETHERITE,
            HTMaterialKeys.SILVER,
            HTMaterialKeys.TIN,
            HTMaterialKeys.NIKOLITE,
        ).forEach {
            // Chunks
            addModItem(builder, it, CHUNK)
            // Purified Ores
            addModItem(builder, it, PURIFIED_ORE)
        }
        addModItem(builder, HTMaterialKeys.NIKOLITE, HTShapeKeys.INGOT)
        addModItem(builder, HTMaterialKeys.NIKOLITE, HTShapeKeys.DUST)
        builder.add(HTMaterialKeys.WOOD, HTShapeKeys.DUST, Registry.ITEM.get(id("sawdust")))
        builder.add(HTMaterialKeys.SULFUR, HTShapeKeys.GEM, IRItemRegistry.SULFUR_CRYSTAL_ITEM)
    }
}
