package io.github.hiiragi283.api.tool

import io.github.hiiragi283.api.extension.TypedIdentifier
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.tool.behavior.HTToolBehavior
import net.minecraft.item.ItemGroup
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Rarity

object HTToolProperties {
    //    ToolMaterial    //

    @JvmField
    val DURABILITY: TypedIdentifier<Int> =
        TypedIdentifier.of(HTModuleType.TOOL.id("durability"))

    @JvmField
    val MINING_SPEED: TypedIdentifier<Float> =
        TypedIdentifier.of(HTModuleType.TOOL.id("mining_speed"))

    @JvmField
    val ATTACK_DAMAGE: TypedIdentifier<Float> =
        TypedIdentifier.of(HTModuleType.TOOL.id("attack_damage"))

    @JvmField
    val MINING_LEVEL: TypedIdentifier<Int> =
        TypedIdentifier.of(HTModuleType.TOOL.id("mining_level"))

    @JvmField
    val ENCHANTABILITY: TypedIdentifier<Int> =
        TypedIdentifier.of(HTModuleType.TOOL.id("enchantability"))

    //    Settings    //

    @JvmField
    val REPAIRMENT: TypedIdentifier<Ingredient> =
        TypedIdentifier.of(HTModuleType.TOOL.id("repairment"))

    @JvmField
    val ITEM_GROUP: TypedIdentifier<ItemGroup> =
        TypedIdentifier.of(HTModuleType.TOOL.id("item_group"))

    @JvmField
    val RARITY: TypedIdentifier<Rarity> =
        TypedIdentifier.of(HTModuleType.TOOL.id("rarity"))

    @JvmField
    val FIREPROOF: TypedIdentifier<Unit> =
        TypedIdentifier.of(HTModuleType.TOOL.id("fireproof"))

    //    HTTool    //

    @JvmField
    val SOUND: TypedIdentifier<SoundEvent> =
        TypedIdentifier.of(HTModuleType.TOOL.id("sound"))

    @JvmField
    val PLAY_SOUND_ON_BREAK: TypedIdentifier<Unit> =
        TypedIdentifier.of(HTModuleType.TOOL.id("play_sound_on_break"))

    @JvmField
    val BEHAVIOR_LIST: TypedIdentifier<List<HTToolBehavior>> =
        TypedIdentifier.of(HTModuleType.TOOL.id("behavior_list"))
}
