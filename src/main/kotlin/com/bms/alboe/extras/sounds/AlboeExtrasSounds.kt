package com.bms.alboe.extras.sounds

import com.bms.alboe.extras.AlboeExtras
import net.minecraft.sounds.SoundEvent

object AlboeExtrasSounds {
    @JvmField
    val PVP_ENABLED = SoundEvent.createVariableRangeEvent(AlboeExtras.id("pvp_enabled"))

    @JvmField
    val PVP_DISABLED = SoundEvent.createVariableRangeEvent(AlboeExtras.id("pvp_disabled"))
}