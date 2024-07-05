package io.github.hiiragi283.mixin;

import io.github.hiiragi283.api.tool.HTBlockReformingRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.HoeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@SuppressWarnings("unchecked")
@Mixin(HoeItem.class)
public abstract class HoeItemMixin {

    @Redirect(method = "useOnBlock", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private <K, V> V ht_api$get(Map<K, V> instance, Object object) {
        return (V) HTBlockReformingRegistry.getTilled((Block) object);
    }

}