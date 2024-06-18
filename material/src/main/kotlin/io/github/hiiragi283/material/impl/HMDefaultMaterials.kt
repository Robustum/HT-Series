package io.github.hiiragi283.material.impl

import io.github.hiiragi283.api.extension.HTColor
import io.github.hiiragi283.api.extension.averageColor
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.item.shape.HTShapeKeys
import io.github.hiiragi283.api.item.shape.HTShapeRegistry
import io.github.hiiragi283.api.material.HTMaterialKeys
import io.github.hiiragi283.api.material.HTMaterialRegistry
import io.github.hiiragi283.api.material.composition.HTElements
import io.github.hiiragi283.api.material.composition.HTMaterialComposition
import io.github.hiiragi283.api.material.content.HTMaterialOre
import io.github.hiiragi283.api.material.property.HTMaterialGemType
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.module.HTPlugin
import java.awt.Color

internal object HMDefaultMaterials : HTPlugin.Material {
    override val modId: String = HTModuleType.MATERIAL.modId
    override val priority: Int = -100

    override fun registerShape(builder: HTShapeRegistry.Builder) {
        // Block
        builder.add(HTShapeKeys.BLOCK)
        builder.add(HTShapeKeys.BRICKS)
        builder.add(HTShapeKeys.LOG)
        builder.add(HTShapeKeys.ORE)
        builder.add(HTShapeKeys.ORE_NETHER, "nether_%s_ore", "%s_ores")
        builder.add(HTShapeKeys.ORE_BLACKSTONE, "blackstone_%s_ore", "%s_ores")
        builder.add(HTShapeKeys.ORE_END, "end_%s_ore", "%s_ores")
        builder.add(HTShapeKeys.ORE_GRAVEL, "gravel_%s_ore", "%s_ores")
        builder.add(HTShapeKeys.ORE_SAND, "sand_%s_ore", "%s_ores")
        builder.add(HTShapeKeys.PLANKS)
        // Item
        builder.add(HTShapeKeys.DUST)
        builder.add(HTShapeKeys.GEAR)
        builder.add(HTShapeKeys.GEM)
        builder.add(HTShapeKeys.INGOT)
        builder.add(HTShapeKeys.NUGGET)
        builder.add(HTShapeKeys.PLATE)
        builder.add(HTShapeKeys.RAW_CHUNK)
        builder.add(HTShapeKeys.ROD)
    }

    override fun registerMaterial(builder: HTMaterialRegistry.Builder) {
        // H
        builder.addSimpleMaterial(HTMaterialKeys.HYDROGEN, HTElements.H to 2)
            .addVirtualFluid(HTFluidPhase.GAS)
        builder.addSimpleMaterial(HTMaterialKeys.WATER, HTElements.H to 2, HTElements.O to 1)
            .color(HTColor.BLUE)
        // He
        builder.addSimpleMaterial(HTMaterialKeys.HELIUM, HTElements.He to 1)
            .addVirtualFluid(HTFluidPhase.GAS)
        // Li
        builder.addMetalMaterial(HTMaterialKeys.LITHIUM, HTElements.Li)
            .add3x3StorageBlock()
            .addMetalItems(false)
        // Be
        builder.addMetalMaterial(HTMaterialKeys.BERYLLIUM, HTElements.Be, false)
            .add3x3StorageBlock()
            .addMetalItems(false)
        builder.addGemMaterial(
            HTMaterialKeys.EMERALD,
            HTMaterialComposition.molecular(
                HTElements.Be to 3,
                HTElements.Al to 2,
                HTElements.Si to 6,
                HTElements.O to 18,
            ),
            HTMaterialGemType.EMERALD,
        ).color(HTColor.GREEN)
            .addGemItems(true)
            .removeItem(HTShapeKeys.GEM)
        // B
        // C
        builder.addSimpleMaterial(HTMaterialKeys.CARBON, HTElements.C to 1)
            .addItem(HTShapeKeys.DUST)
            .addItem(HTShapeKeys.PLATE)
        builder.addSimpleMaterial(HTMaterialKeys.ASHES, HTElements.C)
            .color(HTColor.DARK_GRAY)
            .addItem(HTShapeKeys.DUST)
            .addItem(HTShapeKeys.PLATE)
        builder.addSimpleMaterial(HTMaterialKeys.CHARCOAL, HTElements.C to 1)
            .color(averageColor(HTColor.BLACK to 7, HTColor.YELLOW to 1))
            .add3x3StorageBlock()
            .addItem(HTShapeKeys.DUST)
        builder.addGemMaterial(HTMaterialKeys.COAL, HTElements.C, HTMaterialGemType.COAL)
            .addItem(HTShapeKeys.DUST)
        builder.addGemMaterial(
            HTMaterialKeys.COKE,
            HTMaterialComposition.molecular(HTElements.C to 1),
            HTMaterialGemType.COAL,
        ).color(HTColor.DARK_GRAY)
            .add3x3StorageBlock()
            .addGemItems(false)
        builder.addGemMaterial(
            HTMaterialKeys.DIAMOND,
            HTMaterialComposition.molecular(HTElements.C to 1),
            HTMaterialGemType.DIAMOND,
        ).color(HTColor.AQUA)
            .addGemItems(true)
            .removeItem(HTShapeKeys.GEM)
        builder.addSimpleMaterial(HTMaterialKeys.MILK, HTElements.C, HTElements.H, HTElements.O)
            .addVirtualFluid(HTFluidPhase.LIQUID)
        builder.addPolymerMaterial(HTMaterialKeys.RUBBER, HTElements.C to 5, HTElements.H to 6)
            .color(averageColor(HTColor.BLACK, HTColor.DARK_GRAY))
            .formula("CC(=C)C=C")
            .add3x3StorageBlock()
            .addMetalItems(false)
        builder.addSimpleMaterial(HTMaterialKeys.WOOD, HTElements.C, HTElements.H, HTElements.O)
            .color(averageColor(HTColor.DARK_GRAY to 2, HTColor.RED to 1, HTColor.YELLOW to 1))
            .formula("(C, H, O)")
            .addItem(HTShapeKeys.DUST)
            .addItem(HTShapeKeys.PLATE)
        // N
        builder.addSimpleMaterial(HTMaterialKeys.NITROGEN, HTElements.N to 2)
            .addVirtualFluid(HTFluidPhase.GAS)
        builder.addSimpleMaterial(HTMaterialKeys.AMMONIA, HTElements.N to 1, HTElements.H to 3)
            .addVirtualFluid(HTFluidPhase.GAS)
        builder.addGemMaterial(
            HTMaterialKeys.NITER,
            HTMaterialComposition.molecular(HTElements.H to 1, HTElements.NO3 to 1),
            HTMaterialGemType.LAPIS,
        )
            .addGemOreBlock(HTMaterialOre.Rock.STONE)
            .addGemItems(false)
        builder.addSimpleMaterial(HTMaterialKeys.NITRIC_ACID, HTElements.H to 1, HTElements.NO3 to 1)
            .addVirtualFluid(HTFluidPhase.LIQUID)
        // O
        builder.addSimpleMaterial(HTMaterialKeys.OXYGEN, HTElements.O to 2)
            .addVirtualFluid(HTFluidPhase.GAS)
        // F
        builder.addSimpleMaterial(HTMaterialKeys.FLUORINE, HTElements.F to 2)
            .addVirtualFluid(HTFluidPhase.GAS)
        builder.addGemMaterial(
            HTMaterialKeys.FLUORITE,
            HTMaterialComposition.molecular(HTElements.Ca to 1, HTElements.F to 2),
            HTMaterialGemType.LAPIS,
        ).color(HTColor.GREEN)
            .addGemOreBlock(HTMaterialOre.Rock.STONE)
            .addGemOreBlock(HTMaterialOre.Rock.NETHER)
            .addGemItems(false)
        builder.addSimpleMaterial(HTMaterialKeys.HYDROGEN_FLUORIDE, HTElements.H to 1, HTElements.F to 1)
            .color(averageColor(HTColor.GREEN, HTColor.AQUA))
            .addVirtualFluid(HTFluidPhase.GAS)
        // Ne
        // Na
        builder.addMetalMaterial(HTMaterialKeys.SODIUM, HTElements.Na)
            .add3x3StorageBlock()
            .addMetalItems(false)
        builder.addSimpleMaterial(HTMaterialKeys.SODIUM_HYDROXIDE, HTElements.Na to 1, HTElements.OH to 1)
            .color(HTColor.WHITE)
            .addItem(HTShapeKeys.DUST)
        // Mg
        // Al
        builder.addMetalMaterial(HTMaterialKeys.ALUMINUM, HTElements.Al)
            .add3x3StorageBlock()
            .addMetalItems(true)
        builder.addSimpleMaterial(HTMaterialKeys.ALUMINA, HTElements.Al2O3 to 1)
            .color(HTColor.WHITE)
            .addItem(HTShapeKeys.DUST)
        builder.getOrCreate(HTMaterialKeys.BAUXITE)
            .composition(HTMaterialComposition.hydrate(HTElements.Al2O3 to 1, waterCount = 2))
            .color(averageColor(HTColor.BLACK to 1, HTColor.DARK_RED to 2, HTColor.GOLD to 1))
            .addGemOreBlock(HTMaterialOre.Rock.STONE, 2)
            .addItem(HTShapeKeys.DUST)
        builder.addSimpleMaterial(HTMaterialKeys.BRICK, HTElements.Al2O3)
            .color(averageColor(HTColor.DARK_RED to 2, HTColor.GOLD to 1, HTColor.DARK_GRAY to 2))
            .addMetalItems(true)
            .removeItem(HTShapeKeys.INGOT)
            .removeItem(HTShapeKeys.NUGGET)
        builder.addSimpleMaterial(HTMaterialKeys.CLAY, HTElements.Al2O3)
            .color(Color(0xa4a8b8))
            .addItem(HTShapeKeys.DUST)
            .addMetalItems(true)
            .removeItem(HTShapeKeys.INGOT)
            .removeItem(HTShapeKeys.NUGGET)
        builder.addGemMaterial(
            HTMaterialKeys.SAPPHIRE,
            HTElements.Al2O3,
            HTMaterialGemType.RUBY,
        ).color(HTColor.BLUE)
            .addGemOreBlock(HTMaterialOre.Rock.STONE, 3)
            .add3x3StorageBlock(3)
            .addGemItems(true)
        builder.addGemMaterial(
            HTMaterialKeys.RUBY,
            HTElements.Al2O3,
            HTMaterialGemType.RUBY,
        ).color(HTColor.RED)
            .addGemOreBlock(HTMaterialOre.Rock.STONE, 3)
            .add3x3StorageBlock(3)
            .addGemItems(true)
        // Si
        builder.addMetalMaterial(HTMaterialKeys.SILICON, HTElements.Si, false)
            .add3x3StorageBlock(2)
            .addMetalItems(false)
        builder.addGemMaterial(HTMaterialKeys.FLINT, HTElements.SiO2, HTMaterialGemType.FLINT)
            .color(averageColor(HTColor.BLACK, HTColor.GRAY))
            .addGemItems(false)
            .removeItem(HTShapeKeys.GEM)
        builder.addGemMaterial(HTMaterialKeys.GLASS, HTElements.SiO2, HTMaterialGemType.QUARTZ).color(HTColor.WHITE)
            .addGemItems(false)
        builder.addGemMaterial(
            HTMaterialKeys.LAPIS,
            HTMaterialComposition.mixture(HTElements.SiO2),
            HTMaterialGemType.LAPIS,
        ).color(HTColor.BLUE)
            .addGemItems(false)
            .removeItem(HTShapeKeys.GEM)
        builder.addSimpleMaterial(HTMaterialKeys.LAVA, HTElements.SiO2)
            .color(averageColor(HTColor.DARK_RED, HTColor.GOLD))
        builder.addGemMaterial(HTMaterialKeys.QUARTZ, HTElements.SiO2, HTMaterialGemType.QUARTZ).color(HTColor.WHITE)
            .addGemItems(false)
            .removeItem(HTShapeKeys.GEM)
        builder.addMetalMaterial(HTMaterialKeys.REFINED_SILICON, HTElements.Si, true)
            .add3x3StorageBlock(2)
            .addMetalItems(false)
        builder.addSimpleMaterial(HTMaterialKeys.SLAG, HTElements.SiO2)
            .color(HTColor.DARK_GRAY)
            .addItem(HTShapeKeys.DUST)
        // P
        builder.addSimpleMaterial(HTMaterialKeys.PHOSPHORUS, HTElements.P to 1)
            .addItem(HTShapeKeys.DUST)
        // S
        builder.addGemMaterial(
            HTMaterialKeys.SULFUR,
            HTElements.S to 8,
            HTMaterialGemType.FLINT,
        )
            .addGemOreBlock(HTMaterialOre.Rock.STONE)
            .addGemItems(false)
        builder.addSimpleMaterial(HTMaterialKeys.SULFUR_DIOXIDE, HTElements.S to 1, HTElements.O to 2)
            .addVirtualFluid(HTFluidPhase.GAS)
        builder.addSimpleMaterial(HTMaterialKeys.SULFUR_TRIOXIDE, HTElements.S to 1, HTElements.O to 3)
            .addVirtualFluid(HTFluidPhase.GAS)
        builder.addSimpleMaterial(HTMaterialKeys.SULFURIC_ACID, HTElements.H to 2, HTElements.SO4 to 1)
            .addVirtualFluid(HTFluidPhase.LIQUID)
        // Cl
        builder.addSimpleMaterial(HTMaterialKeys.CHLORINE, HTElements.Cl to 2)
            .addVirtualFluid(HTFluidPhase.GAS)
        builder.addSimpleMaterial(HTMaterialKeys.HYDROGEN_CHLORIDE, HTElements.H to 1, HTElements.Cl to 1)
            .addVirtualFluid(HTFluidPhase.GAS)
        builder.addGemMaterial(
            HTMaterialKeys.SALT,
            HTMaterialComposition.molecular(HTElements.Na to 1, HTElements.Cl to 1),
            HTMaterialGemType.FLINT,
        )
            .color(HTColor.WHITE)
            .addGemOreBlock(HTMaterialOre.Rock.STONE)
            .addGemItems(false)
        // Ar

        // K
        // Ca
        // Ti
        // Cr
        // Mn
        // Fe
        builder.addMetalMaterial(HTMaterialKeys.IRON, HTElements.Fe)
            .addMetalItems(true)
            .removeItem(HTShapeKeys.INGOT)
            .removeItem(HTShapeKeys.NUGGET)
        builder.addMetalMaterial(
            HTMaterialKeys.STEEl,
            HTMaterialComposition.mixture(HTElements.Fe, HTElements.C),
            false,
        )
            .color(HTColor.DARK_GRAY)
            .formula("(Fe, C)")
            .add3x3StorageBlock(2)
            .addMetalItems(true)
        builder.addSimpleMaterial(HTMaterialKeys.RAW_STEEl, HTElements.Fe, HTElements.C)
            .color(HTColor.DARK_GRAY)
            .formula("(Fe, C)")
            .addItem(HTShapeKeys.DUST)
        // Co
        // Ni
        // Cu
        // Zn

        // Ag
        // Sn

        // W
        // Ir
        // Pt
        // Au
        // Hg
        // Pb

        // U
        // Pu
    }

    /*override fun registerMaterial(builder: HTMaterialRegistry.Builder) {
        // 1st Period
        builder.addSimpleMaterial(HTMaterialKeys.HYDROGEN, HTElements.H to 2)
        builder.addSimpleMaterial(HTMaterialKeys.HELIUM, HTElements.He to 1)
        // 2nd Period
        builder.addMetalMaterial(HTMaterialKeys.LITHIUM, HTElements.Li)
        builder.addMetalMaterial(HTMaterialKeys.BERYLLIUM, HTElements.Be, false)
        builder.addSimpleMaterial(HTMaterialKeys.CARBON, HTElements.C to 1)
        builder.addSimpleMaterial(HTMaterialKeys.NITROGEN, HTElements.N to 2)
        builder.addSimpleMaterial(HTMaterialKeys.OXYGEN, HTElements.O to 2)
        builder.addSimpleMaterial(HTMaterialKeys.FLUORINE, HTElements.F to 2)
        // 3rd Period
        builder.addMetalMaterial(HTMaterialKeys.SODIUM, HTElements.Na)
        builder.addMetalMaterial(HTMaterialKeys.MAGNESIUM, HTElements.Mg, false)
        builder.addMetalMaterial(HTMaterialKeys.ALUMINUM, HTElements.Al)
        // builder.addAlternativeName(HTMaterialKeys.ALUMINUM, "aluminium")
        builder.addMetalMaterial(HTMaterialKeys.SILICON, HTElements.Si)
        builder.addSimpleMaterial(HTMaterialKeys.PHOSPHORUS, HTElements.P to 1)
        builder.addSimpleMaterial(HTMaterialKeys.SULFUR, HTElements.S to 8)
        builder.addSimpleMaterial(HTMaterialKeys.CHLORINE, HTElements.Cl to 2)
        // 4th Period
        builder.addMetalMaterial(HTMaterialKeys.POTASSIUM, HTElements.K)
        builder.addMetalMaterial(HTMaterialKeys.CALCIUM, HTElements.Ca)
        builder.addMetalMaterial(HTMaterialKeys.TITANIUM, HTElements.Ti)
        builder.addMetalMaterial(HTMaterialKeys.CHROMIUM, HTElements.Cr)
        // builder.addAlternativeName(HTMaterialKeys.CHROMIUM, "chrome")
        builder.addMetalMaterial(HTMaterialKeys.MANGANESE, HTElements.Mn, false)
        builder.addMetalMaterial(HTMaterialKeys.IRON, HTElements.Fe)
        builder.addMetalMaterial(HTMaterialKeys.COBALT, HTElements.Co)
        builder.addMetalMaterial(HTMaterialKeys.NICKEL, HTElements.Ni)
        builder.addMetalMaterial(HTMaterialKeys.COPPER, HTElements.Cu)
        builder.addMetalMaterial(HTMaterialKeys.ZINC, HTElements.Zn)
        // 5th Period
        builder.addMetalMaterial(HTMaterialKeys.SILVER, HTElements.Ag)
        builder.addMetalMaterial(HTMaterialKeys.TIN, HTElements.Sn)

        // 6th Period
        builder.addMetalMaterial(HTMaterialKeys.TUNGSTEN, HTElements.W, false)
        builder.addMetalMaterial(HTMaterialKeys.IRIDIUM, HTElements.Ir)
        builder.addMetalMaterial(HTMaterialKeys.PLATINUM, HTElements.Pt)
        builder.addMetalMaterial(HTMaterialKeys.GOLD, HTElements.Au)
        builder.addMetalMaterial(HTMaterialKeys.MERCURY, HTElements.Hg)
        builder.addMetalMaterial(HTMaterialKeys.LEAD, HTElements.Pb, false)
        // 7th Period
        builder.addMetalMaterial(HTMaterialKeys.URANIUM, HTElements.U)
        builder.addMetalMaterial(HTMaterialKeys.PLUTONIUM, HTElements.Pu)
        // Vanilla - Fluids
        builder.getOrCreate(HTMaterialKeys.WATER).run {
            composition = HTMaterialComposition.molecular(mapOf(HTElements.H to 2, HTElements.O to 1)) {
                color = HTColor.BLUE
            }
        }

        builder.getOrCreate(HTMaterialKeys.LAVA).run {
            composition = HTMaterialComposition.molecular(mapOf(HTElements.SiO2 to 1)) {
                color = averageColor(HTColor.DARK_RED, HTColor.GOLD)
            }
        }
        builder.getOrCreate(HTMaterialKeys.MILK).run {
            composition = HTMaterialComposition.mixture()
        }
        // Vanilla - Gems
        builder.addGemMaterial(
            HTMaterialKeys.AMETHYST,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = averageColor(HTColor.BLUE, HTColor.LIGHT_PURPLE)
            },
            HTMaterialGemType.AMETHYST,
        )

        builder.addGemMaterial(
            HTMaterialKeys.DIAMOND,
            HTMaterialComposition.molecular(HTElements.C to 1) {
                color = HTColor.AQUA
            },
            HTMaterialGemType.DIAMOND,
        )

        builder.addGemMaterial(
            HTMaterialKeys.ENDER_PEARL,
            HTMaterialComposition.molecular {
                color = averageColor(HTColor.DARK_GREEN, HTColor.BLUE)
            },
            HTMaterialGemType.EMERALD,
        )

        builder.addGemMaterial(
            HTMaterialKeys.EMERALD,
            HTMaterialComposition.molecular(
                HTElements.Be to 3,
                HTElements.Al to 2,
                HTElements.Si to 6,
                HTElements.O to 18,
            ) { color = HTColor.GREEN },
            HTMaterialGemType.EMERALD,
        )

        builder.addGemMaterial(
            HTMaterialKeys.FLINT,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = averageColor(HTColor.BLACK, HTColor.DARK_GRAY)
            },
            HTMaterialGemType.FLINT,
        )

        builder.addGemMaterial(
            HTMaterialKeys.LAPIS,
            HTMaterialComposition.molecular { color = HTColor.BLUE },
            HTMaterialGemType.LAPIS,
        )

        builder.addGemMaterial(
            HTMaterialKeys.QUARTZ,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) { color = HTColor.WHITE },
            HTMaterialGemType.QUARTZ,
        )
        // Vanilla - Metal
        builder.addMetalMaterial(HTMaterialKeys.NETHERITE, HTElements.Nr, false)
        // Vanilla - Raw Materials
        /*builder.getOrCreate(HTMaterialKeys.RAW_IRON)
            .addProperty(HTRawMaterialProperty(HTMaterialKeys.IRON))
            .composition = HTMaterialComposition.mixture(HTElements.Fe)*/
        // Vanilla - Solids
        builder.getOrCreate(HTMaterialKeys.BRICK).run {
            composition = HTMaterialComposition.molecular {
                color = averageColor(HTColor.DARK_RED to 2, HTColor.GOLD to 1, HTColor.DARK_GRAY to 2)
            }
        }

        builder.getOrCreate(HTMaterialKeys.CHARCOAL).run {
            composition = HTMaterialComposition.molecular(HTElements.C to 1) {
                color = averageColor(HTColor.BLACK to 7, HTColor.YELLOW to 1)
            }
        }

        builder.getOrCreate(HTMaterialKeys.CLAY).run {
            composition = HTMaterialComposition.molecular { color = Color(0xa4a8b8) }
        }

        builder.addGemMaterial(HTMaterialKeys.COAL, HTElements.C, HTMaterialGemType.COAL)

        builder.addGemMaterial(HTMaterialKeys.GLASS, HTElements.SiO2, HTMaterialGemType.RUBY)

        builder.getOrCreate(HTMaterialKeys.GLOWSTONE).run {
            composition = HTMaterialComposition.molecular {
                color = averageColor(HTColor.GOLD to 1, HTColor.YELLOW to 2)
            }
        }

        builder.getOrCreate(HTMaterialKeys.NETHER_BRICK).run {
            composition = HTMaterialComposition.molecular {
                color = averageColor(HTColor.BLACK to 4, HTColor.DARK_RED to 1, HTColor.WHITE to 1)
            }
        }

        builder.getOrCreate(HTMaterialKeys.REDSTONE).run {
            composition = HTMaterialComposition.molecular { color = HTColor.DARK_RED }
        }
        // Vanilla - Stones
        builder.addStoneMaterial(
            HTMaterialKeys.STONE,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = HTColor.DARK_GRAY
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.GRANITE,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = averageColor(HTColor.DARK_RED to 1, HTColor.GRAY to 4, HTColor.RED to 1)
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.DIORITE,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = HTColor.GRAY
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.ANDESITE,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = averageColor(HTColor.DARK_GRAY to 7, HTColor.YELLOW to 1)
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.DEEPSLATE,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = averageColor(HTColor.BLACK, HTColor.DARK_GRAY)
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.CALCITE,
            HTMaterialComposition.molecular(HTElements.Ca to 1, HTElements.CO3 to 1),
        )

        builder.addStoneMaterial(
            HTMaterialKeys.TUFF,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = Color(0x4d5d53)
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.OBSIDIAN,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = averageColor(
                    HTColor.BLACK to 4,
                    HTColor.DARK_BLUE to 2,
                    HTColor.DARK_RED to 1,
                    HTColor.WHITE to 1,
                )
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.NETHERRACK,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = averageColor(HTColor.BLACK to 4, HTColor.DARK_RED to 1, HTColor.RED to 3)
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.BASALT,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = averageColor(HTColor.BLACK, HTColor.GRAY)
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.END_STONE,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = averageColor(HTColor.YELLOW to 1, HTColor.WHITE to 3)
            },
        )

        builder.addStoneMaterial(
            HTMaterialKeys.BLACKSTONE,
            HTMaterialComposition.molecular(HTElements.SiO2 to 1) {
                color = HTColor.BLACK
            },
        )
        // Vanilla - Woods
        builder.getOrCreate(HTMaterialKeys.WOOD).run {
            composition = HTMaterialComposition.mixture(HTElements.C, HTElements.H, HTElements.O) {
                color = averageColor(HTColor.DARK_GRAY to 2, HTColor.RED to 1, HTColor.YELLOW to 1)
                formula = "(C, H, O)"
            }
            // type = HTMaterialType.Wood
        }
        // Common - Fluids
        // Common - Gems
        builder.addGemMaterial(
            HTMaterialKeys.CINNABAR,
            HTMaterialComposition.molecular(HTElements.Hg to 1, HTElements.S to 1) {
                color = HTColor.RED
            },
            HTMaterialGemType.EMERALD,
        )

        builder.addGemMaterial(
            HTMaterialKeys.COKE,
            HTMaterialComposition.molecular(HTElements.C to 1) {
                color = HTColor.DARK_GRAY
            },
            HTMaterialGemType.COAL,
        )

        builder.addGemMaterial(
            HTMaterialKeys.OLIVINE,
            HTMaterialComposition.molecular {
                color = averageColor(HTColor.DARK_GREEN, HTColor.GREEN)
            },
            HTMaterialGemType.EMERALD,
        )

        builder.addGemMaterial(
            HTMaterialKeys.PERIDOT,
            HTMaterialComposition.molecular {
                color = averageColor(HTColor.GREEN, HTColor.WHITE)
            },
            HTMaterialGemType.RUBY,
        )

        builder.addGemMaterial(
            HTMaterialKeys.RUBY,
            HTMaterialComposition.molecular(HTElements.Al2O3 to 1) {
                color = averageColor(HTColor.DARK_RED, HTColor.RED)
            },
            HTMaterialGemType.RUBY,
        )

        builder.addGemMaterial(
            HTMaterialKeys.SALT,
            HTMaterialComposition.molecular(HTElements.Na to 1, HTElements.Cl to 1) {
                color = HTColor.WHITE
            },
            HTMaterialGemType.RUBY,
        )

        builder.addGemMaterial(
            HTMaterialKeys.SAPPHIRE,
            HTMaterialComposition.molecular(HTElements.Al2O3 to 1) {
                color = averageColor(HTColor.DARK_BLUE, HTColor.BLUE)
            },
            HTMaterialGemType.RUBY,
        )
        // Common - Metals
        builder.addMetalMaterial(
            HTMaterialKeys.BRASS,
            HTMaterialComposition.molecular(HTElements.Cu to 3, HTElements.Zn to 1) {
                color = HTColor.GOLD
            },
        )

        builder.addMetalMaterial(
            HTMaterialKeys.BRONZE,
            HTMaterialComposition.molecular(HTElements.Cu to 3, HTElements.Sn to 1),
        )

        builder.addMetalMaterial(
            HTMaterialKeys.ELECTRUM,
            HTMaterialComposition.molecular(HTElements.Ag to 1, HTElements.Au to 1) {
                color = averageColor(HTColor.GOLD, HTColor.YELLOW, HTColor.WHITE)
            },
        )

        builder.addMetalMaterial(
            HTMaterialKeys.INVAR,
            HTMaterialComposition.molecular(HTElements.Fe to 2, HTElements.Ni to 1) {
                color = averageColor(HTColor.GREEN to 1, HTColor.GRAY to 3, HTColor.WHITE to 4)
            },
        )

        builder.addMetalMaterial(
            HTMaterialKeys.STAINLESS_STEEL,
            HTMaterialComposition.molecular(
                HTElements.Fe to 6,
                HTElements.Cr to 1,
                HTElements.Mn to 1,
                HTElements.Ni to 1,
            ) { color = averageColor(HTColor.GRAY, HTColor.WHITE) },
        )

        builder.addMetalMaterial(
            HTMaterialKeys.STEEl,
            HTMaterialComposition.mixture(HTElements.Fe, HTElements.C) {
                color = HTColor.DARK_GRAY
                formula = "(Fe, C)"
            },
            false,
        )
        // Common - Solids
        builder.getOrCreate(HTMaterialKeys.ASHES).run {
            composition = HTMaterialComposition.molecular { color = HTColor.DARK_GRAY }
        }

        builder.getOrCreate(HTMaterialKeys.BAUXITE).run {
            composition = HTMaterialComposition.hydrate(
                HTMaterialComposition.molecular(HTElements.Al2O3 to 1),
                2,
            ) { color = averageColor(HTColor.BLACK to 1, HTColor.DARK_RED to 2, HTColor.GOLD to 1) }
        }

        builder.getOrCreate(HTMaterialKeys.RUBBER).apply {
            composition = HTMaterialComposition.polymer(HTElements.C to 5, HTElements.H to 6) {
                color = averageColor(HTColor.BLACK, HTColor.DARK_GRAY)
                formula = "CC(=C)C=C"
            }
        }
        // Common - Stones
        builder.addStoneMaterial(
            HTMaterialKeys.MARBLE,
            HTMaterialComposition.molecular(HTElements.Ca to 1, HTElements.CO3 to 1),
        )
        // Common - Woods
    }*/
}
