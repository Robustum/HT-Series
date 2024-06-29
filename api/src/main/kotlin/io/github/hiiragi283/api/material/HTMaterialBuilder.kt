package io.github.hiiragi283.api.material

import io.github.hiiragi283.api.event.HTMaterialEvent
import io.github.hiiragi283.api.extension.*
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.composition.HTElement
import io.github.hiiragi283.api.material.composition.HTMaterialComposition
import io.github.hiiragi283.api.material.content.HTMaterialStorage
import io.github.hiiragi283.api.material.property.HTMaterialFlags
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.material.type.HTBlockProperty
import io.github.hiiragi283.api.material.type.HTMaterialType
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.minecraft.util.Identifier
import java.awt.Color

class HTMaterialBuilder(
    private val materialKey: HTMaterialKey,
    private val materialType: HTMaterialType,
) : HTPropertyHolder.Mutable {
    private var composition: HTMaterialComposition? = null
    private val properties: MutableMap<TypedIdentifier<*>, Any> = mutableMapOf()
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
            addProperty(HTMaterialProperties.COMPONENT, it.componentMap)
        }
    }

    internal fun build(): HTPropertyHolder = HTPropertyHolder.create(properties) {
        // Event
        HTMaterialEvent.MODIFY_BUILDER.invoker().onBuilding(this@HTMaterialBuilder)
        // Set properties
        getDefaultShape()?.let { addProperty(HTMaterialProperties.DEFAULT_ITEM_SHAPE, it) }
        addProperty(HTMaterialProperties.TYPE, materialType)
        addProperty(HTMaterialProperties.BLOCK_SET, blockSet)
        addProperty(HTMaterialProperties.FLUID_SET, fluidSet)
        addProperty(HTMaterialProperties.ITEM_SET, itemSet)
        initComposition(composition)
        // Validate basic property
        if (getOrDefault(HTMaterialProperties.COLOR, HTColor.WHITE) == HTColor.WHITE) {
            remove(HTMaterialProperties.COLOR)
        }
        if (validateFormula(get(HTMaterialProperties.FORMULA)) == null) {
            remove(HTMaterialProperties.FORMULA)
        }
        if (validateMolar(get(HTMaterialProperties.MOLAR)) == null) {
            remove(HTMaterialProperties.MOLAR)
        }
    }

    //    Property    //

    fun <T : Any> addProperty(id: TypedIdentifier<T>, value: T) = apply {
        set(id, value)
    }

    fun <T : Any> removeProperty(id: TypedIdentifier<T>) = apply {
        remove(id)
    }

    override fun <T : Any> get(id: TypedIdentifier<T>): T? = id.cast(properties[id])

    override fun contains(id: TypedIdentifier<*>): Boolean = id in properties

    override fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit) {
        properties.forEach(action)
    }

    override fun <T : Any> set(id: TypedIdentifier<T>, value: T) {
        properties[id] = value
    }

    override fun remove(id: TypedIdentifier<*>) {
        properties.remove(id)
    }

    //    Property - Flag    //

    fun addFlag(id: Identifier) = addProperty(HTMaterialFlags.getOrCreateFlag(id), Unit)

    fun removeFlag(id: Identifier) = removeProperty(HTMaterialFlags.getOrCreateFlag(id))

    //    Property - Composition    //

    fun mixture(vararg elements: HTElement) = composition(HTMaterialComposition.mixture(*elements))

    fun molecular(vararg pairs: Pair<HTElement, Int>) = composition(HTMaterialComposition.molecular(*pairs))

    fun polymer(vararg pairs: Pair<HTElement, Int>) = composition(HTMaterialComposition.polymer(*pairs))

    fun composition(composition: HTMaterialComposition) = apply {
        this.composition = composition
    }

    fun color(color: Color) = addProperty(HTMaterialProperties.COLOR, color)

    fun formula(formula: String) = formula
        .let(::validateFormula)
        ?.let { addProperty(HTMaterialProperties.FORMULA, it) }
        ?: this

    fun molar(molar: Double) = molar
        .let(::validateMolar)
        ?.let { addProperty(HTMaterialProperties.MOLAR, it) }
        ?: this

    //    Property - Content    //

    fun addBlock(shapeKey: HTShapeKey) = apply { blockSet.add(shapeKey) }

    fun add2x2StorageBlock(miningLevel: Int = 1): HTMaterialBuilder {
        check(materialType is HTBlockProperty) {
            "Could not register with $materialKey because material type $materialType is not implementing HTBlockProperty!"
        }
        return addProperty(
            HTMaterialProperties.STORAGE,
            HTMaterialStorage.createFour(
                materialKey,
                materialType,
                miningLevel,
            ),
        )
            .addProperty(HTMaterialProperties.blockState(HTShapeKeys.BLOCK), singleBlockStateFunction)
    }

    fun add3x3StorageBlock(miningLevel: Int = 1): HTMaterialBuilder {
        check(materialType is HTBlockProperty) {
            "Could not register with $materialKey because material type $materialType is not implementing HTBlockProperty!"
        }
        val blockProperty: HTMaterialStorage = HTMaterialStorage.createNine(
            materialKey,
            materialType,
            miningLevel,
        )
        return addProperty(HTMaterialProperties.STORAGE, blockProperty)
            .addProperty(HTMaterialProperties.blockState(HTShapeKeys.BLOCK), blockProperty.blockStateFunction)
            .addProperty(HTMaterialProperties.itemBlockModel(HTShapeKeys.BLOCK), blockProperty.itemModelFunction)
    }

    fun addFluid(phase: HTFluidPhase) = apply { fluidSet.add(phase) }

    fun addItem(shapeKey: HTShapeKey) = apply { itemSet.add(shapeKey) }

    /*
    //    Property - Block    //

    @JvmOverloads
    fun add2x2StorageBlock(miningLevel: Int = 1) = addStorageBlock(miningLevel, HTStorageBlockRecipe::Four)

    @JvmOverloads
    fun add3x3StorageBlock(miningLevel: Int = 1) = addStorageBlock(miningLevel, HTStorageBlockRecipe::Nine)

    private fun addStorageBlock(miningLevel: Int, propertyBuilder: (HTMaterialKey) -> HTStorageBlockRecipe) = apply {
        val type: HTMaterialStorageBlock.Type = getOrDefault(
            HTMaterialProperties.STORAGE_BLOCK_TYPE,
            HTMaterialStorageBlock.Type.DEFAULT,
        )
        addProperty(
            HTMaterialProperties.blockModel(HTShapeKeys.BLOCK),
            BiConsumer { builder, _ ->
                builder.parentId = HTModuleType.API.id("block/storage/${type.modelName}")
            },
        )
        addProperty(HTMaterialProperties.STORAGE_BLOCK_RECIPE, propertyBuilder(materialKey))
        addBlock(
            HTShapeKeys.BLOCK,
            { _, _ -> HTMaterialStorageBlock(type, miningLevel) },
            { createMaterialBlockItem(it, materialKey, HTShapeKeys.BLOCK) },
        )
    }

    @JvmOverloads
    fun addGemOreBlock(rock: HTMaterialOre.Rock, miningLevel: Int = 1) = apply {
        addOreBlock(HTMaterialOre.Type.GEM, rock, miningLevel)
    }

    @JvmOverloads
    fun addMetalOreBlock(rock: HTMaterialOre.Rock, miningLevel: Int = 1) = apply {
        addOreBlock(HTMaterialOre.Type.METAL, rock, miningLevel)
    }

    fun addOreFeature(
        rock: HTMaterialOre.Rock,
        veinSize: VeinSize,
        height: Height,
        probability: Probability,
    ) = apply { HTMaterialOre.Manager.registerProperty(materialKey, rock, veinSize, height, probability) }

    private fun addOreBlock(type: HTMaterialOre.Type, rock: HTMaterialOre.Rock, miningLevel: Int) {
        val oreShape: HTShapeKey = rock.shapeKey
        // Ore Block
        addBlock(
            oreShape,
            { _, _ -> HTMaterialOre.Block(rock, miningLevel) },
            { createMaterialBlockItem(it, materialKey, oreShape) },
        )
        // Material Properties
        addProperty(HTMaterialProperties.blockLayer(oreShape), RenderLayer.getCutout())
        addProperty(
            HTMaterialProperties.blockLoot(oreShape),
            Function { HTMaterialOre.createLootTable(it, materialKey) },
        )
        addProperty(
            HTMaterialProperties.blockModel(oreShape),
            BiConsumer { builder, _ ->
                builder.parentId = HTModuleType.API.id("block/ore")
                builder.addTexture("stone", Identifier(rock.texId))
                builder.addTexture("ore", HTModuleType.API.id(type.texId))
            },
        )
    }

    fun <T : Block> addBlock(
        shapeKey: HTShapeKey,
        blockFunction: (HTMaterialKey, HTShapeKey) -> T,
        itemFunction: (T) -> BlockItem,
    ) = apply {
        val block: T = blockFunction(materialKey, shapeKey)
        addProperty(HTMaterialProperties.blockContent(shapeKey), lazy { block })
        // addItem(shapeKey) { _, _ -> itemFunction(block) }
        addProperty(
            HTMaterialProperties.itemModel(shapeKey),
            HTItemModelProperty.ofBlock(block, shapeKey),
        )
    }

    //    Property - Fluid    //

    fun addVirtualFluid(phase: HTFluidPhase) = addFluid(phase) { _, _ -> HTVirtualFluid() }

    fun addFluid(phase: HTFluidPhase, function: (HTMaterialKey, HTFluidPhase) -> Fluid) =
        addProperty(HTMaterialProperties.fluidContent(phase), lazy { function(materialKey, phase) })

    fun removeFluid(phase: HTFluidPhase) = removeProperty(HTMaterialProperties.fluidContent(phase))

    //    Property - Item    //

    private fun createMaterialBlockItem(block: Block, materialKey: HTMaterialKey, shapeKey: HTShapeKey): BlockItem =
        HTTextSuppliedBlockItem(
            block,
            HTItemSettings().group(HTApiHolder.Material.apiInstance.itemGroup),
        ) { shapeKey.getTranslatedText(materialKey) }
     */
}
