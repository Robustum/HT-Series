package io.github.hiiragi283.api.material

import io.github.hiiragi283.api.block.HTMaterialStorageBlock
import io.github.hiiragi283.api.event.HTMaterialEvent
import io.github.hiiragi283.api.extension.TypedIdentifier
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.shape.HTShape
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.material.composition.HTElement
import io.github.hiiragi283.api.material.composition.HTMaterialComposition
import io.github.hiiragi283.api.material.property.HTItemModelProperty
import io.github.hiiragi283.api.material.property.HTMaterialGemType
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.module.HTApiHolder
import net.minecraft.block.Block
import net.minecraft.fluid.Fluid
import net.minecraft.item.Item

class HTMaterialRegistry(map: Map<HTMaterialKey, HTMaterial> = mapOf()) :
    Map<HTMaterialKey, HTMaterial> by map {
    fun getOrEmpty(key: HTMaterialKey): HTMaterial = getOrDefault(key, HTMaterial(key, mapOf()))

    val blocks: Collection<Triple<HTMaterial, HTShape, Block>>
        get() = buildList {
            HTApiHolder.Material.apiInstance.shapeRegistry.values.forEach { shape ->
                values.forEach { material ->
                    material.getBlock(shape.key)?.let {
                        add(Triple(material, shape, it))
                    }
                }
            }
        }

    val fluids: Collection<Triple<HTMaterial, HTFluidPhase, Fluid>>
        get() = buildList {
            HTFluidPhase.entries.forEach { phase ->
                values.forEach { material ->
                    material.getFluid(phase)?.let {
                        add(Triple(material, phase, it))
                    }
                }
            }
        }

    val items: Collection<Triple<HTMaterial, HTShape, Item>>
        get() = buildList {
            HTApiHolder.Material.apiInstance.shapeRegistry.values.forEach { shape ->
                values.forEach { material ->
                    material.getItem(shape.key)?.let {
                        add(Triple(material, shape, it))
                    }
                }
            }
        }

    companion object {
        @JvmStatic
        fun create(map: MutableMap<HTMaterialKey, MutableMap<TypedIdentifier<*>, Any>>): HTMaterialRegistry = HTMaterialRegistry(
            map.mapValues { (key: HTMaterialKey, properties: MutableMap<TypedIdentifier<*>, Any>) ->
                HTMaterial.Builder(key, properties).also { builder ->
                    HTMaterialEvent.MODIFY_BUILDER.invoker().onBuilding(builder)
                        /*builder.composition?.run {
                            builder.addPropertyIfAbsent(
                                HTMaterialProperties.COLOR,
                                color,
                            )
                            builder.addPropertyIfAbsent(
                                HTMaterialProperties.FORMULA,
                                HTMaterial.validateFormula(formula),
                            )
                            builder.addPropertyIfAbsent(
                                HTMaterialProperties.MOLAR,
                                HTMaterial.validateMolar(molar),
                            )
                            builder.addProperty(HTMaterialProperties.COMPONENT, componentMap)
                        }*/
                    if (HTMaterial.validateFormula(builder[HTMaterialProperties.FORMULA]) == null) {
                        builder.remove(HTMaterialProperties.FORMULA)
                    }
                    if (HTMaterial.validateMolar(builder[HTMaterialProperties.MOLAR]) == null) {
                        builder.remove(HTMaterialProperties.MOLAR)
                    }
                }
                HTMaterial(key, properties)
            },
        )
    }

    //    Builder    //

    class Builder(private val map: MutableMap<HTMaterialKey, MutableMap<TypedIdentifier<*>, Any>> = mutableMapOf()) {
        fun getOrCreate(key: HTMaterialKey): HTMaterial.Builder = map
            .computeIfAbsent(key) { mutableMapOf() }
            .let { HTMaterial.Builder(key, it) }
            .apply { key.validated = true }

        fun addSimpleMaterial(key: HTMaterialKey, vararg elements: Pair<HTElement, Int>): HTMaterial.Builder =
            addMaterial(key, HTMaterialComposition.molecular(*elements))

        fun addSimpleMaterial(key: HTMaterialKey, vararg elements: HTElement): HTMaterial.Builder =
            addMaterial(key, HTMaterialComposition.mixture(*elements))

        private fun addMaterial(key: HTMaterialKey, composition: HTMaterialComposition): HTMaterial.Builder =
            getOrCreate(key).composition(composition)

        //    Gem    //

        fun addGemMaterial(key: HTMaterialKey, element: HTElement, type: HTMaterialGemType): HTMaterial.Builder =
            addGemMaterial(key, element to 1, type)

        fun addGemMaterial(key: HTMaterialKey, element: Pair<HTElement, Int>, type: HTMaterialGemType): HTMaterial.Builder =
            addGemMaterial(key, HTMaterialComposition.molecular(element), type)

        fun addGemMaterial(key: HTMaterialKey, composition: HTMaterialComposition, type: HTMaterialGemType): HTMaterial.Builder =
            addMaterial(key, composition)
                .addProperty(HTMaterialProperties.DEFAULT_ITEM_SHAPE, HTShapeKeys.GEM)
                .addProperty(HTMaterialProperties.STORAGE_BLOCK_TYPE, HTMaterialStorageBlock.Type.GEM)
                .apply {
                    HTApiHolder.Material.apiInstance.shapeRegistry.keys.forEach { shapeKey ->
                        addProperty(
                            HTMaterialProperties.itemModel(shapeKey),
                            HTItemModelProperty.ofGem(type, shapeKey),
                        )
                    }
                }

        //    Metal    //

        @JvmOverloads
        fun addMetalMaterial(key: HTMaterialKey, element: HTElement, isShiny: Boolean = true): HTMaterial.Builder =
            addMetalMaterial(key, element to 1, isShiny)

        @JvmOverloads
        fun addMetalMaterial(key: HTMaterialKey, element: Pair<HTElement, Int>, isShiny: Boolean = true): HTMaterial.Builder =
            addMetalMaterial(key, HTMaterialComposition.molecular(element), isShiny)

        @JvmOverloads
        fun addMetalMaterial(key: HTMaterialKey, composition: HTMaterialComposition, isShiny: Boolean = true): HTMaterial.Builder =
            addMaterial(key, composition)
                .addProperty(HTMaterialProperties.DEFAULT_ITEM_SHAPE, HTShapeKeys.INGOT)
                .addProperty(
                    HTMaterialProperties.STORAGE_BLOCK_TYPE,
                    if (isShiny) HTMaterialStorageBlock.Type.METAL_SHINY else HTMaterialStorageBlock.Type.METAL_DULL,
                )
                .apply {
                    HTApiHolder.Material.apiInstance.shapeRegistry.keys.forEach { shapeKey ->
                        addProperty(
                            HTMaterialProperties.itemModel(shapeKey),
                            HTItemModelProperty.ofMetal(isShiny, shapeKey),
                        )
                    }
                }

        //    Stone    //

        fun addStoneMaterial(key: HTMaterialKey, composition: HTMaterialComposition): HTMaterial.Builder = addMaterial(key, composition)

        //    Polymer    //

        fun addPolymerMaterial(key: HTMaterialKey, vararg elements: Pair<HTElement, Int>): HTMaterial.Builder =
            addMaterial(key, HTMaterialComposition.polymer(*elements))
    }
}
