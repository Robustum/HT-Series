package io.github.hiiragi283.mixin;

import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {

    @Accessor
    static Set<Block> getEFFECTIVE_BLOCKS() {
        throw new AssertionError();
    }

}