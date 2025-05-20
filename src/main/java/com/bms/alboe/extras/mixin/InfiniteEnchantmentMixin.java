package com.bms.alboe.extras.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.ArrowInfiniteEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArrowInfiniteEnchantment.class)
public abstract class InfiniteEnchantmentMixin extends Enchantment {
    protected InfiniteEnchantmentMixin(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] applicableSlots) {
        super(rarity, category, applicableSlots);
    }

    @WrapMethod(method = "checkCompatibility")
    public boolean checkCompatibility(Enchantment other, Operation<Boolean> original) {
        return super.checkCompatibility(other);
    }
}
