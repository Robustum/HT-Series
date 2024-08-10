package io.github.hiiragi283.engineering.common.item

import io.github.hiiragi283.api.energy.HTEnergyLevel
import io.github.hiiragi283.api.energy.HTEnergyManager
import io.github.hiiragi283.api.energy.HTEnergyType
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.LiteralText
import net.minecraft.util.ActionResult
import net.minecraft.world.World

object HTEnergyMeterItem : Item(FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1)) {

    /*override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        user.abilities.allowFlying = true
        user.isOnGround = true
        return TypedActionResult.success(user.getStackInHand(hand))
    }*/

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val world: World = context.world
        if (world.isClient) return ActionResult.PASS
        HTEnergyType.entries.forEach { type: HTEnergyType ->
            val level: HTEnergyLevel = HTEnergyManager.getLevelOrOff(type, world, context.blockPos, context.side)
            context.player?.sendMessage(LiteralText("$type Level - $level"), false)
        }
        return ActionResult.success(world.isClient)
    }
}