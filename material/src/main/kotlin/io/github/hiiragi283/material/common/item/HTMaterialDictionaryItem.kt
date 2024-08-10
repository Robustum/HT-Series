package io.github.hiiragi283.material.common.item

import io.github.hiiragi283.material.common.screen.HTMaterialDictionaryScreenHandler
import io.github.hiiragi283.material.impl.HTMaterialsAPIImpl
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.TypedActionResult
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

object HTMaterialDictionaryItem : Item(
    FabricItemSettings()
        .group(HTMaterialsAPIImpl.itemGroup)
        .rarity(Rarity.EPIC),
) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack: ItemStack = user.getStackInHand(hand)
        if (!world.isClient) {
            user.openHandledScreen(
                SimpleNamedScreenHandlerFactory({ syncId: Int, _, player: PlayerEntity ->
                    HTMaterialDictionaryScreenHandler(syncId, player)
                }, name),
            )
            return TypedActionResult.success(stack)
        }
        return TypedActionResult.consume(stack)
    }

    override fun toString(): String = "Item{${Registry.ITEM.getId(this)}}"
}