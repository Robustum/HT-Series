package io.github.hiiragi283.material.compat

import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeRegistry
import io.github.hiiragi283.api.module.HTPlugin

@Suppress("unused")
object HMMIPlugin : HTPlugin.Material {
    override val modId: String = "modern_industrialization"
    override val priority: Int = 0

    override fun registerShape(builder: HTShapeRegistry.Builder) {
        listOf(
            "blade",
            "bolt",
            "crushed_dust",
            "curved_plate",
            "double_ingot",
            "drill_head",
            "hot_ingot",
            "large_plate",
            "ring",
            "rotor",
            "tiny_dust",
            "wire",
        ).map(HTShapeKey.Companion::of).forEach(builder::add)
    }

    /*override fun commonSetup() {
        HTRecipeRegisterCallback.EVENT.register { handler ->
            handler.addMIRecipe(
                HTMaterials.id("test_mi"),
                MIRecipeJson.create(MIMachineRecipeTypes.MIXER, 32, 200)
                    .addItemInput(HTPartManagerOld.getDefaultItem(HTElementMaterials.COPPER, HTShapes.Items.DUST)!!, 3)
                    .addItemInput(HTPartManagerOld.getDefaultItem(HTElementMaterials.TIN, HTShapes.Items.DUST)!!, 1)
                    .addItemOutput(HTPartManagerOld.getDefaultItem(HTCommonMaterials.BRONZE, HTShapes.Items.DUST)!!, 4)
                )
        }
    }*/
}
