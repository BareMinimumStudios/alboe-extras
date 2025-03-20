package com.bms.alboe.extras.mixin;

import com.bms.alboe.extras.AlboeExtras;
import com.bms.alboe.extras.Components;
import com.bms.alboe.extras.config.AlboeExtrasConfigModel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import xaero.pac.common.server.api.OpenPACServerAPI;

import java.io.IOException;

@Mixin(ServerPlayer.class)
public class DamageMixin {
    @Unique
    private static boolean isPlayersWithinSameParty(MinecraftServer server, ServerPlayer player1, ServerPlayer player2) {
        OpenPACServerAPI api = OpenPACServerAPI.get(server);

        var partyOne = api.getPartyManager().getPartyByMember(player1.getUUID());
        var partyTwo = api.getPartyManager().getPartyByMember(player2.getUUID());

        return partyOne != null && partyTwo != null && partyOne.getId() == partyTwo.getId();
    }

    @WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"), method = "hurt")
    public boolean hurt(ServerPlayer attacked, DamageSource source, float amount, Operation<Boolean> original) throws IOException {
        AlboeExtrasConfigModel.PartyPvpSetting setting = AlboeExtras.CONFIG.getPartyPvp();

        if (setting != AlboeExtrasConfigModel.PartyPvpSetting.ENABLED)
        {
            try (Level level = attacked.level()) {
                MinecraftServer server = level.getServer();
                Entity attacker = source.getEntity();

                if (attacker instanceof ServerPlayer) {
                    if (DamageMixin.isPlayersWithinSameParty(server, (ServerPlayer) attacker, attacked)) {
                        // global setting to turn off party-pvp
                        if (setting == AlboeExtrasConfigModel.PartyPvpSetting.DISABLED) {
                            return false;
                        }

                        if (!attacker.getComponent(Components.PLAYER_DATA).isPartyPvpEnabled() && !attacked.getComponent(Components.PLAYER_DATA).isPartyPvpEnabled()) {
                            return false;
                        }
                    }
                }
            }
        }

        return original.call(attacked, source, amount);
    }
}