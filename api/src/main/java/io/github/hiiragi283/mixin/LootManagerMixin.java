package io.github.hiiragi283.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.hiiragi283.api.resource.HTRuntimeDataRegistry;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootManager.class)
public abstract class LootManagerMixin {

    @Shadow
    private Map<Identifier, LootTable> tables;

    @Shadow
    @Final
    private LootConditionManager conditionManager;

    @Inject(
            method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;")
    )
    private void ht_api$putTable(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci, @Local ImmutableMap.Builder<Identifier, LootTable> builder) {
        HTRuntimeDataRegistry.getLootTables().forEach((id, table) -> {
            builder.put(id, table.build());
        });
    }

}