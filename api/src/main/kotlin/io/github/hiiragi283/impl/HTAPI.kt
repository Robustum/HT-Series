package io.github.hiiragi283.impl

import io.github.hiiragi283.api.block.entity.HTStorageProvider
import io.github.hiiragi283.api.effect.HTStatusEffect
import io.github.hiiragi283.api.event.HTPlayerEvents
import io.github.hiiragi283.api.extension.registerServerReceiver
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.network.HTCodecSerializer
import io.github.hiiragi283.api.recipe.HTIngredient
import io.github.hiiragi283.api.recipe.HTResult
import io.github.hiiragi283.api.resource.HTRuntimeClientPack
import io.github.hiiragi283.api.resource.MutableResourcePackManager
import io.github.hiiragi283.api.screen.widget.HTFluidWidget
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.item.TooltipContext
import net.minecraft.entity.effect.StatusEffectType
import net.minecraft.item.ItemStack
import net.minecraft.tag.ItemTags
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry

@Suppress("UnstableApiUsage")
internal object HTAPI : ModInitializer, ClientModInitializer {

    private lateinit var creativeFlight: HTStatusEffect
    
    //    Common    //

    override fun onInitialize() {
        registerCodecSerializer("ingredient/item", HTIngredient.ItemImpl.Serializer)
        registerCodecSerializer("ingredient/tag", HTIngredient.TagImpl.Serializer)
        registerCodecSerializer("result/item", HTResult.ItemImpl.Serializer)
        registerCodecSerializer("result/enchantment", HTResult.EnchantImpl.Serializer)

        creativeFlight = Registry.register(
            Registry.STATUS_EFFECT,
            HTModuleType.API.id("creative_flight"),
            HTStatusEffect(StatusEffectType.BENEFICIAL, 0xff003f)
        )

        HTPlayerEvents.START_TICK.register { player ->
            if (player.hasStatusEffect(creativeFlight)) {
                player.abilities.allowFlying = true
                player.setOnFireFor(5)
                player.isOnGround = true
            } else {
                player.abilities.allowFlying = false
                player.abilities.flying = false
            }
        }
        
        registerServerPackets()
        HTLogger.log { it.info("HT API Initialized!") }
    }

    private fun registerCodecSerializer(path: String, serializer: HTCodecSerializer<*>) {
        Registry.register(
            HTCodecSerializer.REGISTRY,
            HTModuleType.API.id(path),
            serializer,
        )
    }

    private fun registerServerPackets() {
        registerServerReceiver(HTFluidWidget.PACKET_CODEC) { server, _, _, context, _ ->
            val (worldId: Identifier, pos: BlockPos, variant: FluidVariant, amount: Long, index: Int) = context
            server.registryManager
                .get(Registry.WORLD_KEY)
                .get(worldId)
                ?.getBlockEntity(pos)
                ?.let { it as? HTStorageProvider }
                ?.getFluidStorage(null)
                ?.getSlot(index)
                ?.apply {
                    this.variant = variant
                    this.amount = amount
                }
        }
    }

    //    Client    //

    override fun onInitializeClient() {
        registerClientPackets()
        (MinecraftClient.getInstance().resourcePackManager as MutableResourcePackManager)
            .`ht_materials$addPackProvider`(HTRuntimeClientPack.Provider)
        HTLogger.log { it.info("HT Runtime Resource Pack registered!") }

        ItemTooltipCallback.EVENT.register { stack: ItemStack, context: TooltipContext, lines: MutableList<Text> ->
            // Tag tooltips (only dev)
            if (FabricLoader.getInstance().isDevelopmentEnvironment) {
                ItemTags.getTagGroup().getTagsFor(stack.item).forEach { id: Identifier ->
                    lines.add(LiteralText(id.toString()).formatted(Formatting.DARK_GRAY))
                }
            }
        }
        HTLogger.log { it.info("HT API Client Initialized!") }
    }

    private fun registerClientPackets() {
        /*registerClientReceiver(HTPropertySync.PACKET_CODEC) { client, _, sync, _ ->
            client.execute {
                val handler: HTScreenHandler = client.player?.currentScreenHandler as? HTScreenHandler ?: return@execute
                if (handler.syncId == sync.syncId) {
                    handler.propertyDelegate?.set(sync.index, sync.value)
                }
            }
        }*/
    }
}