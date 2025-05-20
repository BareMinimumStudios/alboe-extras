package com.bms.alboe.extras.mixin;

import com.bms.alboe.extras.AlboeExtras;
import com.bms.alboe.extras.UtilsKt;
import com.bms.alboe.extras.config.AlboeExtrasConfigModel;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.spell_engine.utils.TargetHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TargetHelper.class)
public class TargetHelperMixin {
    @WrapMethod(method = "allowedToHurt")
    private static boolean isAllowedToHurt(Entity player1, Entity player2, Operation<Boolean> original) {
        AlboeExtrasConfigModel.PartyPvpSetting setting = AlboeExtras.CONFIG.getPartyPvp();

        if (setting != AlboeExtrasConfigModel.PartyPvpSetting.ENABLED) {
            if (player1 instanceof ServerPlayer && player2 instanceof ServerPlayer) {
                if (AlboeExtras.SERVER_PARTY_MANAGER != null && UtilsKt.isPlayersWithinSameParty(AlboeExtras.SERVER_PARTY_MANAGER, (ServerPlayer) player1, (ServerPlayer) player2)) {
                    if (setting == AlboeExtrasConfigModel.PartyPvpSetting.DISABLED) {
                        return false;
                    }

                    if (!UtilsKt.isPartyPvpEnabledForBoth((ServerPlayer) player1, (ServerPlayer) player2)) {
                        return false;
                    }
                }
            }
        }

        return original.call(player1, player2);
    }

    @WrapMethod(method = "getRelation")
    private static TargetHelper.Relation getRelation(LivingEntity attacker, Entity target, Operation<TargetHelper.Relation> original) {
        AlboeExtrasConfigModel.PartyPvpSetting setting = AlboeExtras.CONFIG.getPartyPvp();

        if (setting != AlboeExtrasConfigModel.PartyPvpSetting.ENABLED) {
            if (attacker instanceof ServerPlayer && target instanceof ServerPlayer) {
                if (AlboeExtras.SERVER_PARTY_MANAGER != null && UtilsKt.isPlayersWithinSameParty(AlboeExtras.SERVER_PARTY_MANAGER, (ServerPlayer) attacker, (ServerPlayer) target)) {
                    if (setting == AlboeExtrasConfigModel.PartyPvpSetting.DISABLED) {
                        return TargetHelper.Relation.FRIENDLY;
                    }

                    if (!UtilsKt.isPartyPvpEnabledForBoth((ServerPlayer) attacker, (ServerPlayer) target)) {
                        return TargetHelper.Relation.FRIENDLY;
                    }
                }
            }
        }

        return original.call(attacker, target);
    }
}
