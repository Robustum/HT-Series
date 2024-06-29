package io.github.hiiragi283.impl.mixin;

import io.github.hiiragi283.impl.HTJSonDataLoaderMixin;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Mixin(TagGroupLoader.class)
public abstract class TagGroupLoaderMixin<T> {
    @Shadow
    @Final
    private String dataType;

    @Inject(method = "prepareReload", at = @At("HEAD"), cancellable = true)
    private void ht_materials$prepareReload(ResourceManager manager, Executor prepareExecutor, CallbackInfoReturnable<CompletableFuture<Map<Identifier, Tag.Builder>>> cir) {
        cir.setReturnValue(CompletableFuture.supplyAsync(() -> HTJSonDataLoaderMixin.loadTagMap(manager, dataType), prepareExecutor));
    }

    @Redirect(method = "method_26797", at = @At(value = "INVOKE", target = "Ljava/util/stream/Collectors;joining(Ljava/lang/CharSequence;)Ljava/util/stream/Collector;"))
    private Collector<CharSequence, ?, String> ht_materials$fixErrorMessage(CharSequence delimiter) {
        return Collectors.joining(", \n");
    }
}