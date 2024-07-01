package io.github.hiiragi283.impl

import io.github.hiiragi283.api.block.entity.HTStorageProvider
import io.github.hiiragi283.api.extension.registerClientReceiver
import io.github.hiiragi283.api.extension.registerServerReceiver
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.module.HTModuleType
import io.github.hiiragi283.api.network.HTCodecSerializer
import io.github.hiiragi283.api.recipe.HTIngredient
import io.github.hiiragi283.api.recipe.HTResult
import io.github.hiiragi283.api.resource.HTRuntimeClientPack
import io.github.hiiragi283.api.resource.MutableResourcePackManager
import io.github.hiiragi283.api.screen.HTPropertySync
import io.github.hiiragi283.api.screen.HTScreenHandler
import io.github.hiiragi283.api.screen.widget.HTFluidWidget
import io.github.hiiragi283.api.storage.HTSingleVariantStorage
import io.github.hiiragi283.api.storage.HTSlottedStorage
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

@Suppress("UnstableApiUsage")
object HTAPI : ModInitializer, ClientModInitializer {
    //    Common    //

    override fun onInitialize() {
        registerCodecSerializer("ingredient/item", HTIngredient.ItemImpl.Serializer)
        registerCodecSerializer("ingredient/tag", HTIngredient.TagImpl.Serializer)
        registerCodecSerializer("result/item", HTResult.ItemImpl.Serializer)
        registerCodecSerializer("result/enchantment", HTResult.EnchantImpl.Serializer)

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
            val world: World = server.registryManager
                .get(Registry.WORLD_KEY)
                .get(worldId)
                ?: return@registerServerReceiver
            val storage: HTSlottedStorage<FluidVariant> =
                (world.getBlockEntity(pos) as? HTStorageProvider)
                    ?.getFluidStorage(null)
                    ?: return@registerServerReceiver
            val slotStorage: HTSingleVariantStorage<FluidVariant> =
                storage.getSlot(index) as? HTSingleVariantStorage<FluidVariant> ?: return@registerServerReceiver
            slotStorage.variant = variant
            slotStorage.amount = amount
        }
    }

    //    Client    //

    override fun onInitializeClient() {
        registerClientPackets()
        (MinecraftClient.getInstance().resourcePackManager as MutableResourcePackManager)
            .`ht_materials$addPackProvider`(HTRuntimeClientPack.Provider)
        HTLogger.log { it.info("HT Runtime Resource Pack registered!") }
        HTLogger.log { it.info("HT API Client Initialized!") }
    }

    private fun registerClientPackets() {
        registerClientReceiver(HTPropertySync.PACKET_CODEC) { client, _, sync, _ ->
            client.execute {
                val handler: HTScreenHandler = client.player?.currentScreenHandler as? HTScreenHandler ?: return@execute
                if (handler.syncId == sync.syncId) {
                    handler.propertyDelegate?.set(sync.index, sync.value)
                }
            }
        }
    }
}
