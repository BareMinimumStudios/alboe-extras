package com.bms.alboe.extras.config

import blue.endless.jankson.Comment
import com.bms.alboe.extras.AlboeExtras
import io.wispforest.owo.config.Option
import io.wispforest.owo.config.annotation.Config
import io.wispforest.owo.config.annotation.Modmenu
import io.wispforest.owo.config.annotation.SectionHeader
import io.wispforest.owo.config.annotation.Sync

@Suppress("UNUSED")
@Modmenu(modId = AlboeExtras.MOD_ID)
@Config(name = AlboeExtras.MOD_ID, wrapperName = "AlboeExtrasConfig")
class AlboeExtrasConfigModel {
    enum class PartyPvpSetting {
        ENABLED,
        DISABLED,
        SET_BY_PLAYER
    }

    @SectionHeader("pvpSettings")

    @JvmField
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @Comment("Whether to enable/disable party pvp or allow it to be set by players independently.")
    var partyPvp = PartyPvpSetting.SET_BY_PLAYER
}