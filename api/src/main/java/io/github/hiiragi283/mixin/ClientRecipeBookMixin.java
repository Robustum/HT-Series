package io.github.hiiragi283.mixin;

import io.github.hiiragi283.api.recipe.HTRecipeBookGroupRegistry;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientRecipeBook.class)
public abstract class ClientRecipeBookMixin {
    
    @Inject(method = "getGroupForRecipe", at = @At("HEAD"), cancellable = true)
    private static void ht_api$getGroupForRecipe(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> cir) {
        cir.setReturnValue(HTRecipeBookGroupRegistry.getGroupOrUnknown(recipe));
    }
    
}