package io.github.hiiragi283.api.extension

import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.minecraft.block.AbstractBlock
import net.minecraft.entity.EntityType

private val neverEntity: AbstractBlock.TypedContextPredicate<EntityType<*>> =
    AbstractBlock.TypedContextPredicate<EntityType<*>> { _, _, _, _ -> false }

private val never: AbstractBlock.ContextPredicate =
    AbstractBlock.ContextPredicate { _, _, _ -> false }

fun FabricBlockSettings.disableSpawning() = apply { allowsSpawning(neverEntity) }

fun FabricBlockSettings.nonSolid() = apply { solidBlock(never) }

fun FabricBlockSettings.nonSuffocates() = apply { suffocates(never) }

fun FabricBlockSettings.transparentVision() = apply { blockVision(never) }
