package io.github.hiiragi283.mixin;

import net.minecraft.block.Block;
import net.minecraft.item.HoeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(HoeItem.class)
public interface HoeItemAccessor {

    @Accessor
    static Set<Block> getEFFECTIVE_BLOCKS() {
        throw new AssertionError();
    }

}