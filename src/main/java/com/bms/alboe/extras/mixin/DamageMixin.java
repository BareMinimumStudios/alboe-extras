package com.bms.alboe.extras.mixin;

import com.bms.alboe.extras.AlboeExtras;
import com.bms.alboe.extras.UtilsKt;
import com.bms.alboe.extras.config.AlboeExtrasConfigModel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class DamageMixin {
    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), method = "hurt")
    public boolean hurt(ServerPlayer attacked, DamageSource source, float amount, Operation<Boolean> original) {
        AlboeExtrasConfigModel.PartyPvpSetting setting = AlboeExtras.CONFIG.getPartyPvp();

        if (setting != AlboeExtrasConfigModel.PartyPvpSetting.ENABLED)
        {
            MinecraftServer server = attacked.getServer();
            if (server != null) {
                Entity attacker = source.getEntity();

                if (attacker instanceof ServerPlayer) {
                    if (AlboeExtras.SERVER_PARTY_MANAGER != null && UtilsKt.isPlayersWithinSameParty(AlboeExtras.SERVER_PARTY_MANAGER, (ServerPlayer) attacker, attacked)) {
                        // global setting to turn off party-pvp
                        if (setting == AlboeExtrasConfigModel.PartyPvpSetting.DISABLED) {
                            return false;
                        }

                        if (!UtilsKt.isPartyPvpEnabledForBoth((ServerPlayer) attacker, attacked)) {
                            return false;
                        }
                    }
                }
            }
        }

        return original.call(attacked, source, amount);
    }
}