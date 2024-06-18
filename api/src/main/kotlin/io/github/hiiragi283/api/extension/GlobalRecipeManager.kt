package io.github.hiiragi283.api.extension

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientLoginNetworkHandler
import net.minecraft.recipe.RecipeManager
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld

object GlobalRecipeManager {
    private var server: RecipeManager? = null
    private var client: RecipeManager? = null

    @JvmStatic
    fun get(type: EnvType): RecipeManager = when (type) {
        EnvType.CLIENT -> client
        EnvType.SERVER -> server
    }.let {
        checkNotNull(it) { "GlobalRecipeManager on ${type.name} side is not initialized!" }
    }

    init {
        ServerWorldEvents.LOAD.register(GlobalRecipeManager::onWorldLoaded)
        ClientLoginConnectionEvents.INIT.register(GlobalRecipeManager::onLoginStart)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onWorldLoaded(server: MinecraftServer, world: ServerWorld) {
        GlobalRecipeManager.server = server.recipeManager
    }

    @Suppress("UNUSED_PARAMETER")
    private fun onLoginStart(handler: ClientLoginNetworkHandler, client: MinecraftClient) {
        GlobalRecipeManager.client = checkNotNull(client.networkHandler?.recipeManager) {
            "Could not find client player!"
        }
    }
}
