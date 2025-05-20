package com.bms.alboe.extras.mixin;

import com.bms.alboe.extras.AlboeExtras;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow @Nullable public abstract FoodProperties getFoodProperties();

    @WrapMethod(method = "getUseDuration")
    public int getUseDuration(ItemStack stack, Operation<Integer> original) {
        if (stack.getItem().isEdible()) {
            FoodProperties properties = this.getFoodProperties();
            if (properties != null) {
                return properties.isFastFood() ? AlboeExtras.CONFIG.getFoodSettings().getSnackTime() : AlboeExtras.CONFIG.getFoodSettings().getFoodTime();
            }
        }
        return original.call(stack);
    }
}
