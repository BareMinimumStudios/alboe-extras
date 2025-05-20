package com.bms.alboe.extras.mixin;

import com.bms.alboe.extras.AlboeExtras;
import com.bms.alboe.extras.config.AlboeExtrasConfig;
import com.bms.alboe.extras.config.AlboeExtrasConfig.FoodSettings;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow protected FoodData foodData;

    @Unique
    int eatingCooldown = 0;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (eatingCooldown > 0) --eatingCooldown;
    }

    @Inject(method = "canEat", at = @At("HEAD"), cancellable = true)
    public void canEat(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> cir) {
        FoodSettings foodSettings = AlboeExtras.CONFIG.getFoodSettings();
        if ((!foodData.needsFood() || foodSettings.getForceCooldown()) && eatingCooldown == 0) {
            cir.setReturnValue(true);
            this.eatingCooldown = foodSettings.getCooldown();
            this.eatingCooldown += this.eatingCooldown * foodSettings.getCooldownDifficultyScale() * this.level().getDifficulty().getId();
            cir.cancel();
        }
    }
}
