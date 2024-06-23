package io.github.hiiragi283.api.extension

import net.minecraft.enchantment.Enchantments
import net.minecraft.loot.BinomialLootTableRange
import net.minecraft.loot.ConstantLootTableRange
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.UniformLootTableRange
import net.minecraft.loot.condition.MatchToolLootCondition
import net.minecraft.loot.condition.SurvivesExplosionLootCondition
import net.minecraft.loot.context.LootContextType
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.entry.AlternativeEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.function.ApplyBonusLootFunction
import net.minecraft.predicate.NumberRange
import net.minecraft.predicate.item.EnchantmentPredicate
import net.minecraft.predicate.item.ItemPredicate

inline fun buildLootTable(type: LootContextType = LootContextTypes.BLOCK, builderAction: LootTable.Builder.() -> Unit): LootTable.Builder =
    LootTable.Builder().apply(builderAction)

inline fun buildLootPool(builderAction: LootPool.Builder.() -> Unit): LootPool.Builder = LootPool.Builder().apply(builderAction)

fun LootPool.Builder.rolls(value: Int): LootPool.Builder = rolls(ConstantLootTableRange.create(value))

fun LootPool.Builder.rolls(n: Int, p: Float): LootPool.Builder = rolls(BinomialLootTableRange.create(n, p))

fun LootPool.Builder.rolls(min: Float, max: Float): LootPool.Builder = rolls(UniformLootTableRange.between(min, max))

fun LootPool.Builder.rolls(range: IntRange): LootPool.Builder = rolls(range.first.toFloat(), range.last.toFloat())

fun LootPool.Builder.surviveExplosion(): LootPool.Builder? = conditionally(SurvivesExplosionLootCondition.builder())

fun <T : LeafEntry.Builder<*>> T.withSilkTouch(): LeafEntry.Builder<*> = conditionally(
    MatchToolLootCondition.builder(
        ItemPredicate.Builder.create()
            .enchantment(
                EnchantmentPredicate(
                    Enchantments.SILK_TOUCH,
                    NumberRange.IntRange.atLeast(1),
                ),
            ),
    ),
)

fun <T : LeafEntry.Builder<*>> T.withSilkTouch(alternative: LeafEntry.Builder<*>): AlternativeEntry.Builder =
    withSilkTouch().alternatively(alternative)

fun <T : LeafEntry.Builder<*>> T.withFortune(): LeafEntry.Builder<*> = apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE))
