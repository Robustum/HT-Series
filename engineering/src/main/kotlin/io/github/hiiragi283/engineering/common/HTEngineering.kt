package io.github.hiiragi283.engineering.common

import io.github.hiiragi283.api.energy.HTEnergyLevel
import io.github.hiiragi283.api.energy.HTEnergyType
import io.github.hiiragi283.api.event.HTTagEvents
import io.github.hiiragi283.api.module.HTLogger
import io.github.hiiragi283.api.tag.HTBlockTags
import io.github.hiiragi283.engineering.common.init.HEInit
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback
import net.minecraft.block.Blocks

object HTEngineering : ModInitializer {
    override fun onInitialize() {
        HEInit

        LootTableLoadingCallback.EVENT.register { resourceManager, lootManager, id, supplier, setter ->

        }

        HTTagEvents.BLOCK.register { handler ->
            // Energy - Heat
            handler.add(
                HTEnergyType.HEAT.getBlockTag(HTEnergyLevel.LOW),
                Blocks.TORCH,
                Blocks.WALL_TORCH,
            )
            handler.add(
                HTEnergyType.HEAT.getBlockTag(HTEnergyLevel.MEDIUM),
                Blocks.FIRE,
                Blocks.LAVA,
            )
            // Soil
            handler.add(
                HTBlockTags.REPLACED_WITH_PEAT,
                Blocks.COARSE_DIRT,
                Blocks.DIRT,
                Blocks.GRASS,
                Blocks.MYCELIUM,
                Blocks.PODZOL,
            )
            handler.add(
                HTBlockTags.REPLACED_WITH_LIGNITE,
            )
            handler.add(
                HTBlockTags.REPLACED_WITH_COAL,
            )
            handler.add(
                HTBlockTags.REPLACED_WITH_BITUMEN,
            )
        }

        HTLogger.log { it.info("HT Engineering Initialized!") }
    }
}