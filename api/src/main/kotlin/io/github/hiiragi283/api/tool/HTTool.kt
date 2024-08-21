package io.github.hiiragi283.api.tool

import com.google.common.collect.Multimap
import io.github.hiiragi283.api.extension.EntityAttributeMapBuilder
import io.github.hiiragi283.api.tool.behavior.HTToolBehavior
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool
import net.minecraft.block.BlockState
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.sound.SoundCategory
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@JvmDefaultWithCompatibility
interface HTTool : DynamicAttributeTool {
    val toolProperty: HTToolProperty
    val toolClass: HTToolClass

    val behaviorList: List<HTToolBehavior>
        get() = buildList {
            addAll(toolClass.defaultBehaviors)
            toolProperty.ifPresent(HTToolProperties.BEHAVIOR_LIST, ::addAll)
        }

    fun postHitWithTool(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        behaviorList.forEach { behavior -> behavior.postHit(stack, target, attacker) }
        stack.damage(1, attacker) { it.sendToolBreakStatus(it.activeHand) }
        return true
    }

    fun postMineWithTool(
        stack: ItemStack,
        world: World,
        state: BlockState,
        pos: BlockPos,
        miner: LivingEntity,
    ): Boolean {
        if (!world.isClient) {
            behaviorList.forEach { behavior -> behavior.postMine(stack, world, state, pos, miner) }
            if (state.getHardness(world, pos) > 0.0) {
                stack.damage(1, miner) { it.sendToolBreakStatus(it.activeHand) }
            }
            if (miner is PlayerEntity && HTToolProperties.PLAY_SOUND_ON_BREAK in toolProperty) {
                toolProperty.ifPresent(HTToolProperties.SOUND) { sound ->
                    world.playSound(miner, pos, sound, SoundCategory.BLOCKS, 1.0f, 1.0f)
                }
            }
        }
        return true
    }

    fun useOnBlockWithTool(context: ItemUsageContext): ActionResult {
        for (behavior: HTToolBehavior in behaviorList) {
            if (behavior.useOnBlock(context) == ActionResult.SUCCESS) {
                return ActionResult.SUCCESS
            }
        }
        return ActionResult.PASS
    }

    fun useWithTool(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack: ItemStack = user.getStackInHand(hand)
        if (!world.isClient) {
        }
        for (behavior: HTToolBehavior in behaviorList) {
            if (behavior.use(world, user, hand).result == ActionResult.SUCCESS) {
                return TypedActionResult.success(stack, world.isClient)
            }
        }
        return TypedActionResult.pass(stack)
    }

    override fun getDynamicModifiers(
        slot: EquipmentSlot,
        stack: ItemStack,
        user: LivingEntity?,
    ): Multimap<EntityAttribute, EntityAttributeModifier> = EntityAttributeMapBuilder.create {
        behaviorList.forEach { behavior -> behavior.addEntityAttributes(slot, stack, user, this) }
    }
}
