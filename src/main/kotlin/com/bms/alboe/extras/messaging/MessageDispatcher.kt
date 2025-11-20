package com.bms.alboe.extras.messaging

import com.bms.alboe.extras.alboe
import net.minecraft.world.entity.player.Player
import toni.immersivemessages.api.ImmersiveMessage
import toni.immersivemessages.api.SoundEffect

object MessageDispatcher {
    fun partyPvpEnabledPlayer(player: String): ImmersiveMessage {
        return ImmersiveMessage.toast(5F, "⚔ Heads up!", "$player has enabled their PVP!")
            .fadeIn(1F)
            .typewriter(1.5F, false)
            .fadeOut(1F)
            .sound(SoundEffect.NONE)
            .alboe()
    }

    fun partyPvpDisabledPlayer(player: String): ImmersiveMessage {
        return ImmersiveMessage.toast(5F, "⚔ Heads up!", "$player has disabled their PVP!")
            .fadeIn(1F)
            .typewriter(1.5F, false)
            .fadeOut(1F)
            .sound(SoundEffect.NONE)
            .alboe()
    }
}