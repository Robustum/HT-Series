package io.github.hiiragi283.api.material

import io.github.hiiragi283.api.block.HTMaterialStorageBlock
import io.github.hiiragi283.api.extension.TypedIdentifier
import io.github.hiiragi283.api.fluid.HTVirtualFluid
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.HTItemSettings
import io.github.hiiragi283.api.item.HTTextSuppliedBlockItem
import io.github.hiiragi283.api.item.HTTextSuppliedItem
import io.github.hiiragi283.api.item.shape.HTShapeKey
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.composition.HTMaterialComposition
import io.github.hiiragi283.api.material.content.HTMaterialOre
import io.github.hiiragi283.api.material.content.HTMaterialOre.Height
import io.github.hiiragi283.api.material.content.HTMaterialOre.Probability
import io.github.hiiragi283.api.material.content.HTMaterialOre.VeinSize
import io.github.hiiragi283.api.material.property.*
import io.github.hiiragi283.api.module.HTApiHolder
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.property.HTPropertyHolder
import net.minecraft.block.Block
import net.minecraft.client.render.RenderLayer
import net.minecraft.fluid.Fluid
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Identifier
import java.awt.Color
import java.util.function.BiConsumer
import java.util.function.Function

data class HTMaterial(
    override val materialKey: HTMaterialKey,
    private val properties: Map<TypedIdentifier<*>, Any>,
) : HTPropertyHolder, HTMaterialKeyable {
    val isEmpty: Boolean = properties.isEmpty()

    fun getBlock(shapeKey: HTShapeKey): Block? = get(HTMaterialProperties.blockContent(shapeKey))?.value

    fun getFluid(phase: HTFluidPhase): Fluid? = get(HTMaterialProperties.fluidContent(phase))?.value

    fun getItem(shapeKey: HTShapeKey): Item? = get(HTMaterialProperties.itemContent(shapeKey))?.value

    //    HTPropertyHolder    //

    override fun <T : Any> get(id: TypedIdentifier<T>): T? = id.cast(properties[id])

    override fun contains(id: TypedIdentifier<*>): Boolean = id in properties

    override fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit) {
        properties.forEach(action)
    }

    //    Builder    //

    class Builder(
        private val materialKey: HTMaterialKey,
        private val properties: MutableMap<TypedIdentifier<*>, Any>,
    ) : HTPropertyHolder.Mutable {
        companion object {
            private val COMPOSITION: TypedIdentifier<HTMaterialComposition> =
                TypedIdentifier.of(HTModuleType.API.id("composition"))
        }

        //    Property    //

        fun <T : Any> addProperty(id: TypedIdentifier<T>, value: T): Builder = apply {
            properties[id] = value
        }

        fun <T : Any> addPropertyIfAbsent(id: TypedIdentifier<T>, value: T): Builder = apply {
            properties.computeIfAbsent(id) { value }
        }

        fun <T : Any> removeProperty(id: TypedIdentifier<T>): Builder = apply {
            properties.remove(id)
        }

        //    Property - Flag    //

        fun addFlag(id: Identifier): Builder = addProperty(HTMaterialFlags.getOrCreateFlag(id), Unit)

        fun removeFlag(id: Identifier): Builder = removeProperty(HTMaterialFlags.getOrCreateFlag(id))

        //    Property - Composition    //

        internal var composition: HTMaterialComposition? = get(COMPOSITION)

        fun composition(composition: HTMaterialComposition): Builder = this
            .color(composition.color)
            .formula(composition.formula)
            .molar(composition.molar)
            .addProperty(HTMaterialProperties.COMPONENT, composition.componentMap)

        fun color(color: Color): Builder = addProperty(HTMaterialProperties.COLOR, color)

        fun formula(formula: String): Builder = formula
            .let(::validateFormula)
            ?.let { addProperty(HTMaterialProperties.FORMULA, it) }
            ?: this

        fun molar(molar: Double): Builder = molar
            .let(::validateMolar)
            ?.let { addProperty(HTMaterialProperties.MOLAR, it) }
            ?: this

        override fun <T : Any> get(id: TypedIdentifier<T>): T? = id.cast(properties[id])

        override fun contains(id: TypedIdentifier<*>): Boolean = id in properties

        override fun forEachProperties(action: (TypedIdentifier<*>, Any) -> Unit) {
            properties.forEach(action)
        }

        override fun <T : Any> set(id: TypedIdentifier<T>, value: T) {
            addProperty(id, value)
        }

        override fun remove(id: TypedIdentifier<*>) {
            removeProperty(id)
        }

        //    Property - Block    //

        @JvmOverloads
        fun add2x2StorageBlock(miningLevel: Int = 1): Builder = addStorageBlock(miningLevel, HTStorageBlockRecipe::Four)

        @JvmOverloads
        fun add3x3StorageBlock(miningLevel: Int = 1): Builder = addStorageBlock(miningLevel, HTStorageBlockRecipe::Nine)

        private fun addStorageBlock(miningLevel: Int, propertyBuilder: (HTMaterialKey) -> HTStorageBlockRecipe): Builder = apply {
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
        fun addGemOreBlock(rock: HTMaterialOre.Rock, miningLevel: Int = 1): Builder = apply {
            addOreBlock(HTMaterialOre.Type.GEM, rock, miningLevel)
            // Chunk Item
            if (HTMaterialProperties.itemContent(HTShapeKeys.RAW_CHUNK) !in properties) {
                addItem(HTShapeKeys.RAW_CHUNK)
            }
        }

        @JvmOverloads
        fun addMetalOreBlock(rock: HTMaterialOre.Rock, miningLevel: Int = 1): Builder = apply {
            addOreBlock(HTMaterialOre.Type.METAL, rock, miningLevel)
            // Chunk Item
            if (HTMaterialProperties.itemContent(HTShapeKeys.RAW_CHUNK) !in properties) {
                addItem(HTShapeKeys.RAW_CHUNK)
            }
        }

        fun addOreFeature(
            rock: HTMaterialOre.Rock,
            veinSize: VeinSize,
            height: Height,
            probability: Probability,
        ): Builder = apply { HTMaterialOre.Manager.registerProperty(materialKey, rock, veinSize, height, probability) }

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
        ): Builder = apply {
            val block: T = blockFunction(materialKey, shapeKey)
            addProperty(HTMaterialProperties.blockContent(shapeKey), lazy { block })
            addItem(shapeKey) { _, _ -> itemFunction(block) }
            addProperty(
                HTMaterialProperties.itemModel(shapeKey),
                HTItemModelProperty.ofBlock(block, shapeKey),
            )
        }

        fun removeBlock(shapeKey: HTShapeKey): Builder = removeProperty(HTMaterialProperties.blockContent(shapeKey))
            .removeItem(shapeKey)

        //    Property - Fluid    //

        fun addVirtualFluid(phase: HTFluidPhase): Builder = addFluid(phase) { _, _ -> HTVirtualFluid() }

        fun addFluid(phase: HTFluidPhase, function: (HTMaterialKey, HTFluidPhase) -> Fluid): Builder =
            addProperty(HTMaterialProperties.fluidContent(phase), lazy { function(materialKey, phase) })

        fun removeFluid(phase: HTFluidPhase): Builder = removeProperty(HTMaterialProperties.fluidContent(phase))

        //    Property - Item    //

        fun addMetalItems(isAdvanced: Boolean): Builder = apply {
            buildList {
                add(HTShapeKeys.DUST)
                add(HTShapeKeys.INGOT)
                add(HTShapeKeys.NUGGET)
                add(HTShapeKeys.PLATE)
                if (isAdvanced) {
                    add(HTShapeKeys.GEAR)
                    add(HTShapeKeys.ROD)
                }
            }.forEach(::addItem)
        }

        fun addGemItems(isAdvanced: Boolean): Builder = apply {
            buildList {
                add(HTShapeKeys.DUST)
                add(HTShapeKeys.GEM)
                add(HTShapeKeys.PLATE)
                if (isAdvanced) {
                    add(HTShapeKeys.GEAR)
                    add(HTShapeKeys.ROD)
                }
            }.forEach(::addItem)
        }

        private fun createMaterialItem(materialKey: HTMaterialKey, shapeKey: HTShapeKey): Item = HTTextSuppliedItem(
            HTItemSettings().group(HTApiHolder.Material.apiInstance.itemGroup),
        ) { shapeKey.getTranslatedText(materialKey) }

        private fun createMaterialBlockItem(block: Block, materialKey: HTMaterialKey, shapeKey: HTShapeKey): BlockItem =
            HTTextSuppliedBlockItem(
                block,
                HTItemSettings().group(HTApiHolder.Material.apiInstance.itemGroup),
            ) { shapeKey.getTranslatedText(materialKey) }

        fun addItem(shapeKey: HTShapeKey, function: (HTMaterialKey, HTShapeKey) -> Item = ::createMaterialItem): Builder =
            addProperty(HTMaterialProperties.itemContent(shapeKey), lazy { function(materialKey, shapeKey) })

        fun removeItem(shapeKey: HTShapeKey): Builder = removeProperty(HTMaterialProperties.itemContent(shapeKey))
    }

    //    Tooltip    //

    companion object {
        @JvmStatic
        fun appendTooltip(
            material: HTMaterial,
            materialTranslatable: HTMaterialTranslatable? = null,
            stack: ItemStack = ItemStack.EMPTY,
            lines: MutableList<Text> = mutableListOf(),
        ): MutableList<Text> = appendTooltip(TooltipContext(material, materialTranslatable, stack, lines))

        @JvmStatic
        fun appendTooltip(context: TooltipContext): MutableList<Text> {
            val (material: HTMaterial, materialTranslatable: HTMaterialTranslatable?, _: ItemStack, lines: MutableList<Text>) = context
            // Title
            lines.add(TranslatableText("tooltip.ht_materials.material.title"))
            // Name
            val name: String =
                materialTranslatable?.getTranslatedName(material.materialKey) ?: material.materialKey.translatedName
            lines.add(TranslatableText("tooltip.ht_materials.material.name", name))
            // HTShapeType
            // lines.add(TranslatableText("tooltip.ht_materials.material.type", material.type.translatedName))
            // Formula
            material[HTMaterialProperties.FORMULA]?.let { formula: String ->
                lines.add(TranslatableText("tooltip.ht_materials.material.formula", formula))
            }
            // Molar Mass
            material[HTMaterialProperties.MOLAR]?.let { molar: Double ->
                lines.add(TranslatableText("tooltip.ht_materials.material.molar", molar))
            }
            // Tooltip from Properties
            material.forEachProperties { _, property ->
                if (property is HTTooltipProperty) {
                    property.appendTooltip(context)
                }
            }
            return lines
        }

        @JvmStatic
        fun validateFormula(formula: String?): String? = formula?.takeIf { it.isNotEmpty() && it.isNotBlank() }

        @JvmStatic
        fun validateMolar(molar: Double?): Double? = molar?.takeIf { it > 0.0 }?.let { "%.1f".format(it).toDouble() }
    }

    data class TooltipContext(
        val material: HTMaterial,
        val materialTranslatable: HTMaterialTranslatable?,
        val stack: ItemStack,
        val lines: MutableList<Text>,
    ) {
        constructor(material: HTMaterial, lines: MutableList<Text>) : this(material, null, ItemStack.EMPTY, lines)
    }
}
