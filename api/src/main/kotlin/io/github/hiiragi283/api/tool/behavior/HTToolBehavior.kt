package io.github.hiiragi283.api.tool.behavior

import io.github.hiiragi283.api.extension.EntityAttributeMapBuilder
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.tool.HTBlockReformingRegistry
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.minecraft.block.BlockState
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.SimpleRegistry
import net.minecraft.world.World

@JvmDefaultWithCompatibility
interface HTToolBehavior {
    companion object {
        @JvmField
        val REGISTRY: SimpleRegistry<HTToolBehavior> = FabricRegistryBuilder
            .createSimple(HTToolBehavior::class.java, HTModuleType.API.id("tool_behavior"))
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister()

        @JvmField
        val FIRE_IGNITING: HTToolBehavior = register(
            "fire_igniting",
            HTItemDelegatedBehavior(Items.FLINT_AND_STEEL),
        )

        @JvmField
        val HOT_TILLING: HTToolBehavior = register(
            "hoe_tilling",
            HTBlockReformingBehavior(SoundEvents.ITEM_HOE_TILL, HTBlockReformingRegistry::getTilled),
        )

        @JvmField
        val LOG_STRIPPING: HTToolBehavior = register(
            "log_stripping",
            HTLogStrippingBehavior,
        )

        @JvmField
        val SHOVEL_FLATTENING: HTToolBehavior = register(
            "shovel_flattening",
            HTBlockReformingBehavior(SoundEvents.ITEM_SHOVEL_FLATTEN, HTBlockReformingRegistry::getFlattened),
        )

        @JvmField
        val TORCH_PLACING: HTToolBehavior = register(
            "torch_placing",
            HTTorchPlacingBehavior,
        )

        private fun register(path: String, behavior: HTToolBehavior): HTToolBehavior =
            Registry.register(REGISTRY, HTModuleType.TOOL.id(path), behavior)
    }

    fun postHit(stack: ItemStack, target: LivingEntity, attacker: LivingEntity) {}

    fun postMine(
        stack: ItemStack,
        world: World,
        state: BlockState,
        pos: BlockPos,
        miner: LivingEntity,
    ) {}

    fun canDisableShield(
        tool: ItemStack,
        shield: ItemStack,
        blocker: LivingEntity,
        attacker: LivingEntity,
    ): Boolean = false

    fun useOnBlock(context: ItemUsageContext): ActionResult = ActionResult.PASS

    fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> = TypedActionResult.fail(user.getStackInHand(hand))

    fun appendTooltip(
        stack: ItemStack,
        world: World?,
        tooltip: MutableList<Text>,
        context: TooltipContext,
    ) {}

    fun addEntityAttributes(
        slot: EquipmentSlot,
        stack: ItemStack,
        user: LivingEntity?,
        builder: EntityAttributeMapBuilder,
    ) {
    }
}
