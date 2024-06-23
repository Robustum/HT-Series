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
import io.github.hiiragi283.api.material.type.HTMaterialType
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.module.HTPlugin
import java.awt.Color

internal object HMDefaultMaterials : HTPlugin.Material {
    override val modId: String = HTModuleType.MATERIAL.modId
    override val priority: Int = -100

    override fun registerShape(builder: HTShapeRegistry.Builder) {
        // Block
        builder.createBlockShape(HTShapeKeys.BLOCK)
            .addBlacklist(
                HTMaterialKeys.COAL,
                HTMaterialKeys.DIAMOND,
                HTMaterialKeys.EMERALD,
                HTMaterialKeys.GOLD,
                HTMaterialKeys.IRON,
                HTMaterialKeys.NETHERITE,
                HTMaterialKeys.QUARTZ,
                HTMaterialKeys.REDSTONE,
            )
        builder.createBlockShape(HTShapeKeys.BRICKS)
        builder.createBlockShape(HTShapeKeys.LOG)
            .addBlacklist(
                HTMaterialKeys.WOOD,
            )
        builder.createBlockShape(HTShapeKeys.ORE)
            .addBlacklist(
                HTMaterialKeys.COAL,
                HTMaterialKeys.DIAMOND,
                HTMaterialKeys.EMERALD,
                HTMaterialKeys.GOLD,
                HTMaterialKeys.IRON,
                HTMaterialKeys.REDSTONE,
            )
        builder.createBlockShape(HTShapeKeys.ORE_NETHER, "nether_%s_ore", "%s_ores")
            .addBlacklist(
                HTMaterialKeys.QUARTZ,
            )
        builder.createBlockShape(HTShapeKeys.ORE_BLACKSTONE, "blackstone_%s_ore", "%s_ores")
        builder.createBlockShape(HTShapeKeys.ORE_END, "end_%s_ore", "%s_ores")
        builder.createBlockShape(HTShapeKeys.ORE_GRAVEL, "gravel_%s_ore", "%s_ores")
        builder.createBlockShape(HTShapeKeys.ORE_SAND, "sand_%s_ore", "%s_ores")
        builder.createBlockShape(HTShapeKeys.PLANKS)
        // Fluid
        HTFluidPhase.LIQUID.addBlacklist(HTMaterialKeys.WATER, HTMaterialKeys.LAVA)
        // Item
        builder.createItemShape(HTShapeKeys.DUST)
            .addBlacklist(
                HTMaterialKeys.REDSTONE,
                HTMaterialKeys.GLOWSTONE,
            )
        builder.createItemShape(HTShapeKeys.GEAR)
        builder.createItemShape(HTShapeKeys.GEM)
            .addBlacklist(
                HTMaterialKeys.DIAMOND,
                HTMaterialKeys.EMERALD,
                HTMaterialKeys.FLINT,
                HTMaterialKeys.LAPIS,
                HTMaterialKeys.QUARTZ,
            )

        builder.createItemShape(HTShapeKeys.INGOT)
            .addBlacklist(
                HTMaterialKeys.GOLD,
                HTMaterialKeys.IRON,
                HTMaterialKeys.NETHERITE,
            )
        builder.createItemShape(HTShapeKeys.NUGGET)
            .addBlacklist(
                HTMaterialKeys.GOLD,
                HTMaterialKeys.IRON,
            )
        builder.createItemShape(HTShapeKeys.PLATE)
        builder.createItemShape(HTShapeKeys.RAW_CHUNK)
        builder.createItemShape(HTShapeKeys.ROD)
    }

    override fun registerMaterial(builder: HTMaterialRegistry.Builder) {
        // H
        builder.createGas(HTMaterialKeys.HYDROGEN)
            .molecular(HTElements.H to 2)
        builder.createLiquid(HTMaterialKeys.WATER)
            .molecular(HTElements.H to 2, HTElements.O to 1)
            .color(HTColor.BLUE)
        // REMOVE WATER FLUID
        // He
        builder.createGas(HTMaterialKeys.HELIUM)
            .molecular(HTElements.He to 1)
        // Li
        builder.createMetal(HTMaterialKeys.LITHIUM, false)
            .molecular(HTElements.Li to 1)
            .add3x3StorageBlock()
        // Be
        builder.createMetal(HTMaterialKeys.BERYLLIUM, false)
            .molecular(HTElements.Be to 1)
            .add3x3StorageBlock(2)
        builder.createGem(HTMaterialKeys.EMERALD, HTMaterialType.Gem.EMERALD)
            .molecular(
                HTElements.Be to 3,
                HTElements.Al to 2,
                HTElements.Si to 6,
                HTElements.O to 18,
            )
            .color(HTColor.GREEN)
        // B
        // C
        builder.createSolid(HTMaterialKeys.CARBON)
            .molecular(HTElements.C to 1)
        builder.createSolid(HTMaterialKeys.ASHES)
            .mixture(HTElements.C)
            .color(HTColor.DARK_GRAY)
        builder.createSolid(HTMaterialKeys.CHARCOAL)
            .mixture(HTElements.C)
            .color(averageColor(HTColor.BLACK to 7, HTColor.YELLOW to 1))
        builder.createSolid(HTMaterialKeys.COAL)
            .mixture(HTElements.C)
        builder.createGem(HTMaterialKeys.COKE, HTMaterialType.Gem.COAL)
            .mixture(HTElements.C)
            .color(HTColor.DARK_GRAY)
            .add3x3StorageBlock()
        builder.createGem(HTMaterialKeys.DIAMOND, HTMaterialType.Gem.DIAMOND)
            .molecular(HTElements.C to 1)
            .color(HTColor.AQUA)
        builder.createLiquid(HTMaterialKeys.MILK)
            .mixture(HTElements.C, HTElements.H, HTElements.O)
        builder.createSolid(HTMaterialKeys.RUBBER)
            .polymer(HTElements.C to 5, HTElements.H to 6)
            .color(averageColor(HTColor.BLACK, HTColor.DARK_GRAY))
            .formula("CC(=C)C=C")
            .add3x3StorageBlock(0)
        builder.createWood(HTMaterialKeys.WOOD)
            .mixture(HTElements.C, HTElements.H, HTElements.O)
            .color(averageColor(HTColor.DARK_GRAY to 2, HTColor.RED to 1, HTColor.YELLOW to 1))
        // N
        builder.createGas(HTMaterialKeys.NITROGEN)
            .molecular(HTElements.N to 2)
        builder.createGas(HTMaterialKeys.AMMONIA)
            .molecular(HTElements.N to 1, HTElements.H to 3)
        builder.createGem(HTMaterialKeys.NITER, HTMaterialType.Gem.LAPIS)
            .molecular(HTElements.H to 1, HTElements.NO3 to 1)
        builder.createLiquid(HTMaterialKeys.NITRIC_ACID)
            .molecular(HTElements.H to 1, HTElements.NO3 to 1)
        // O
        builder.createGas(HTMaterialKeys.OXYGEN)
            .molecular(HTElements.O to 2)
        // F
        builder.createGas(HTMaterialKeys.FLUORINE)
            .molecular(HTElements.F to 2)
        builder.createGem(HTMaterialKeys.FLUORITE, HTMaterialType.Gem.LAPIS)
            .molecular(HTElements.Ca to 1, HTElements.F to 2)
            .color(HTColor.GREEN)
        builder.createGas(HTMaterialKeys.HYDROGEN_FLUORIDE)
            .molecular(HTElements.H to 1, HTElements.F to 1)
            .color(averageColor(HTColor.GREEN, HTColor.AQUA))
        // Ne
        // Na
        builder.createMetal(HTMaterialKeys.SODIUM, false)
            .molecular(HTElements.Na to 1)
        builder.createSolid(HTMaterialKeys.SODIUM_HYDROXIDE)
            .molecular(HTElements.Na to 1, HTElements.OH to 1)
            .color(HTColor.WHITE)
        // Mg
        // Al
        builder.createMetal(HTMaterialKeys.ALUMINUM)
            .molecular(HTElements.Al to 1)
        builder.createSolid(HTMaterialKeys.ALUMINA)
            .molecular(HTElements.Al2O3 to 1)
            .color(HTColor.WHITE)
        builder.createSolid(HTMaterialKeys.BAUXITE)
            .composition(HTMaterialComposition.hydrate(HTElements.Al2O3 to 1, waterCount = 2))
            .color(averageColor(HTColor.BLACK to 1, HTColor.DARK_RED to 2, HTColor.GOLD to 1))
        builder.createSolid(HTMaterialKeys.BRICK)
            .mixture(HTElements.Al2O3)
            .color(averageColor(HTColor.DARK_RED to 2, HTColor.GOLD to 1, HTColor.DARK_GRAY to 2))
        builder.createSolid(HTMaterialKeys.CLAY)
            .mixture(HTElements.Al2O3)
            .color(Color(0xa4a8b8))
        builder.createGem(HTMaterialKeys.SAPPHIRE, HTMaterialType.Gem.RUBY)
            .molecular(HTElements.Al2O3 to 1)
            .color(HTColor.BLUE)
        builder.createGem(HTMaterialKeys.RUBY, HTMaterialType.Gem.RUBY)
            .molecular(HTElements.Al2O3 to 1)
            .color(HTColor.RED)
        // Si
        builder.createMetal(HTMaterialKeys.SILICON, false)
            .molecular(HTElements.Si to 1)
        builder.createGem(HTMaterialKeys.FLINT, HTMaterialType.Gem.FLINT)
            .mixture(HTElements.SiO2)
            .color(averageColor(HTColor.BLACK, HTColor.GRAY))
        builder.createGem(HTMaterialKeys.GLASS, HTMaterialType.Gem.QUARTZ)
            .mixture(HTElements.SiO2)
            .color(HTColor.WHITE)
        builder.createGem(HTMaterialKeys.LAPIS, HTMaterialType.Gem.LAPIS)
            .mixture(HTElements.SiO2)
            .color(HTColor.BLUE)
        builder.createLiquid(HTMaterialKeys.LAVA)
            .mixture(HTElements.SiO2)
            .color(averageColor(HTColor.DARK_RED, HTColor.GOLD))
        builder.createGem(HTMaterialKeys.QUARTZ, HTMaterialType.Gem.QUARTZ)
            .molecular(HTElements.SiO2 to 1)
            .color(HTColor.WHITE)
        builder.createMetal(HTMaterialKeys.REFINED_SILICON)
            .molecular(HTElements.Si to 1)
        builder.createSolid(HTMaterialKeys.SLAG)
            .mixture(HTElements.SiO2)
            .color(HTColor.DARK_GRAY)
        // P
        builder.createSolid(HTMaterialKeys.PHOSPHORUS)
            .molecular(HTElements.P to 1)
        // S
        builder.createGem(HTMaterialKeys.SULFUR, HTMaterialType.Gem.FLINT)
            .molecular(HTElements.S to 8)
        builder.createGas(HTMaterialKeys.SULFUR_DIOXIDE)
            .molecular(HTElements.S to 1, HTElements.O to 2)
        builder.createGas(HTMaterialKeys.SULFUR_TRIOXIDE)
            .molecular(HTElements.S to 1, HTElements.O to 3)
        builder.createLiquid(HTMaterialKeys.SULFURIC_ACID)
            .molecular(HTElements.H to 2, HTElements.SO4 to 1)
        // Cl
        builder.createGas(HTMaterialKeys.CHLORINE)
            .molecular(HTElements.Cl to 2)
        builder.createGas(HTMaterialKeys.HYDROGEN_CHLORIDE)
            .molecular(HTElements.H to 1, HTElements.Cl to 1)
        builder.createGem(HTMaterialKeys.SALT, HTMaterialType.Gem.FLINT)
            .molecular(HTElements.Na to 1, HTElements.Cl to 1)
            .color(HTColor.WHITE)
        // Ar

        // K
        // Ca
        // Ti
        // Cr
        // Mn
        // Fe
        builder.createMetal(HTMaterialKeys.IRON)
            .molecular(HTElements.Fe to 1)
        builder.createMetal(HTMaterialKeys.STEEl, false)
            .mixture(HTElements.Fe, HTElements.C)
            .color(HTColor.DARK_GRAY)
        builder.createSolid(HTMaterialKeys.RAW_STEEl)
            .mixture(HTElements.Fe, HTElements.C)
            .color(HTColor.DARK_GRAY)
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
            HTMaterialTypeNew.Gem.AMETHYST,
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
