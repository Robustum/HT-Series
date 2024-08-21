package io.github.hiiragi283.api.material.new

import io.github.hiiragi283.api.extension.HTColor
import io.github.hiiragi283.api.extension.singleBlockStateFunction
import io.github.hiiragi283.api.extension.validateFormula
import io.github.hiiragi283.api.extension.validateMolar
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.composition.HTElement
import io.github.hiiragi283.api.material.composition.HTMaterialComposition
import io.github.hiiragi283.api.material.content.HTMaterialStorageContent
import io.github.hiiragi283.api.material.property.HTMaterialFlags
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.material.type.HTBlockProperty
import io.github.hiiragi283.api.material.type.HTMaterialType
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.minecraft.util.Identifier
import java.awt.Color

class HTMaterialBuilderNew(
    private val materialKey: HTMaterialKey,
    private val materialType: HTMaterialType,
) : HTPropertyHolder.Builder(mutableMapOf()) {
    private var composition: HTMaterialComposition? = null
    private val blockSet: MutableSet<HTShapeKey> = HashSet(materialType.blockSet)
    private val fluidSet: MutableSet<HTFluidPhase> = HashSet(materialType.fluidSet)
    private val itemSet: MutableSet<HTShapeKey> = HashSet(materialType.itemSet)

    private fun getDefaultShape(): HTShapeKey? = when {
        HTShapeKeys.INGOT in itemSet && HTShapeKeys.GEM in itemSet ->
            throw IllegalStateException("Could not include both shape INGOT and GEM!")

        HTShapeKeys.INGOT in itemSet -> HTShapeKeys.INGOT
        HTShapeKeys.GEM in itemSet -> HTShapeKeys.GEM
        else -> null
    }

    private fun initComposition(composition: HTMaterialComposition?) {
        composition?.let {
            if (HTMaterialProperties.COLOR !in this) color(it.color)
            if (HTMaterialProperties.FORMULA !in this) formula(it.formula)
            if (HTMaterialProperties.MOLAR !in this) molar(it.molar)
            set(HTMaterialProperties.COMPONENT, it.componentMap)
        }
    }

    internal fun build(): HTMaterial {
        // Event
        // HTMaterialEvent.MODIFY_BUILDER.invoker().onBuilding(this@HTMaterialBuilderNew)
        // Set properties
        setIfNonNull(HTMaterialProperties.DEFAULT_ITEM_SHAPE, getDefaultShape())
        set(HTMaterialProperties.TYPE, materialType)
        set(HTMaterialProperties.BLOCK_SET, blockSet)
        set(HTMaterialProperties.FLUID_SET, fluidSet)
        set(HTMaterialProperties.ITEM_SET, itemSet)
        initComposition(composition)
        // Validate basic property
        removeIf(HTMaterialProperties.COLOR) { it == HTColor.WHITE }
        removeIfNull(HTMaterialProperties.FORMULA) { validateFormula(it) }
        removeIfNull(HTMaterialProperties.MOLAR) { validateMolar(it) }
        return HTMaterial(this)
    }

    //    Property - Flag    //

    fun addFlag(id: Identifier) {
        set(HTMaterialFlags.getOrCreateFlag(id), Unit)
    }

    fun removeFlag(id: Identifier) {
        remove(HTMaterialFlags.getOrCreateFlag(id))
    }

    //    Property - Composition    //

    fun mixture(vararg elements: HTElement) {
        composition(HTMaterialComposition.mixture(*elements))
    }

    fun molecular(vararg pairs: Pair<HTElement, Int>) {
        composition(HTMaterialComposition.molecular(*pairs))
    }

    fun polymer(vararg pairs: Pair<HTElement, Int>) {
        composition(HTMaterialComposition.polymer(*pairs))
    }

    fun composition(composition: HTMaterialComposition) {
        this.composition = composition
    }

    fun color(color: Color) {
        set(HTMaterialProperties.COLOR, color)
    }

    fun formula(formula: String) {
        setIfNonNull(HTMaterialProperties.FORMULA, validateFormula(formula))
    }

    fun molar(molar: Double) {
        setIfNonNull(HTMaterialProperties.MOLAR, validateMolar(molar))
    }

    //    Property - Content    //

    fun addBlock(shapeKey: HTShapeKey) {
        blockSet.add(shapeKey)
    }

    fun add2x2StorageBlock(miningLevel: Int = 1) {
        check(materialType is HTBlockProperty) {
            "Could not register with $materialKey because material type $materialType is not implementing HTBlockProperty!"
        }
        set(
            HTMaterialProperties.STORAGE,
            HTMaterialStorageContent.createFour(
                materialKey,
                materialType,
                miningLevel,
            ),
        )
        set(HTMaterialProperties.blockState(HTShapeKeys.BLOCK), singleBlockStateFunction)
    }

    fun add3x3StorageBlock(miningLevel: Int = 1) {
        check(materialType is HTBlockProperty) {
            "Could not register with $materialKey because material type $materialType is not implementing HTBlockProperty!"
        }
        val blockProperty: HTMaterialStorageContent = HTMaterialStorageContent.createNine(
            materialKey,
            materialType,
            miningLevel,
        )
        set(HTMaterialProperties.STORAGE, blockProperty)
        set(HTMaterialProperties.blockState(HTShapeKeys.BLOCK), blockProperty.blockStateFunction)
        set(HTMaterialProperties.itemBlockModel(HTShapeKeys.BLOCK), blockProperty.itemModelFunction)
    }

    fun addFluid(phase: HTFluidPhase) {
        fluidSet.add(phase)
    }

    fun addItem(shapeKey: HTShapeKey) {
        itemSet.add(shapeKey)
    }
}
