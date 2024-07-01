package io.github.hiiragi283.mixin;

import io.github.hiiragi283.api.extension.TypedIdentifier;
import io.github.hiiragi283.api.item.HTItemSettings;
import io.github.hiiragi283.api.property.HTItemProperties;
import io.github.hiiragi283.api.property.HTPropertyHolder;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(Item.class)
public abstract class ItemMixin implements HTPropertyHolder {
    
    @org.spongepowered.asm.mixin.Mutable
    @Final
    @Shadow
    protected ItemGroup group;

    @org.spongepowered.asm.mixin.Mutable
    @Final
    @Shadow
    private Rarity rarity;

    @org.spongepowered.asm.mixin.Mutable
    @Final
    @Shadow
    private Item recipeRemainder;

    @org.spongepowered.asm.mixin.Mutable
    @Final
    @Shadow
    private int maxDamage;

    @org.spongepowered.asm.mixin.Mutable
    @Final
    @Shadow
    private int maxCount;

    @org.spongepowered.asm.mixin.Mutable
    @Final
    @Shadow
    private @Nullable FoodComponent foodComponent;

    @org.spongepowered.asm.mixin.Mutable
    @Final
    @Shadow
    private boolean fireproof;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void ht_api$init(Item.Settings settings, CallbackInfo ci) {
        if (settings instanceof HTItemSettings) {
            // Store current value
            HTItemSettings itemSettings = (HTItemSettings) settings;
            this.propertyMap = itemSettings.getPropertyMap();
            // Invoke Event
            // HTPropertyRegisterEvent.ITEM.invoker().register(new HTPropertyRegisterEvent.Handler(propertyMap));
            // Reload Properties
            this.group = get(HTItemProperties.GROUP);
            this.rarity = getOrDefault(HTItemProperties.RARITY, Rarity.COMMON);
            this.recipeRemainder = get(HTItemProperties.RECIPE_REMAINDER);
            this.maxDamage = getOrDefault(HTItemProperties.MAX_DAMAGE, 0);
            this.maxCount = getOrDefault(HTItemProperties.MAX_COUNT, 64);
            this.foodComponent = get(HTItemProperties.FOOD_COMPONENT);
            this.fireproof = getOrDefault(HTItemProperties.FIREPROOF, false);
        }
    }

    //    HTPropertyHolder    //

    @Unique
    private Map<TypedIdentifier<?>, Object> propertyMap;

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Nullable
    @Override
    public <T> T get(@NotNull TypedIdentifier<T> id) {
        return id.cast(propertyMap.get(id));
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public boolean contains(@NotNull TypedIdentifier<?> id) {
        return propertyMap.containsKey(id);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void forEachProperties(@NotNull Function2<? super TypedIdentifier<?>, Object, Unit> action) {
        propertyMap.forEach(action::invoke);
    }
}