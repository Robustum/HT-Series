package io.github.hiiragi283.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import io.github.hiiragi283.api.resource.HTRuntimeDataRegistry;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", ordinal = 1, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private void ht_api$putRecipe(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci, Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2) {
        HTRuntimeDataRegistry.INSTANCE.getRecipes().forEach((id, recipe) -> map2.computeIfAbsent(recipe.getType(), type -> ImmutableMap.builder()).put(id, recipe));
    }

}