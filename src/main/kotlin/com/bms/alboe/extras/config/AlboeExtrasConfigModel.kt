package com.bms.alboe.extras.config

import blue.endless.jankson.Comment
import com.bms.alboe.extras.AlboeExtras
import io.wispforest.owo.config.Option
import io.wispforest.owo.config.Option.SyncMode
import io.wispforest.owo.config.annotation.Config
import io.wispforest.owo.config.annotation.Expanded
import io.wispforest.owo.config.annotation.Modmenu
import io.wispforest.owo.config.annotation.Nest
import io.wispforest.owo.config.annotation.RangeConstraint
import io.wispforest.owo.config.annotation.SectionHeader
import io.wispforest.owo.config.annotation.Sync

@Suppress("UNUSED")
@Modmenu(modId = AlboeExtras.MOD_ID)
@Config(name = AlboeExtras.MOD_ID, wrapperName = "AlboeExtrasConfig")
class AlboeExtrasConfigModel {
    @JvmField
    @Sync(SyncMode.OVERRIDE_CLIENT)
    @Comment("Whether to enable/disable party pvp or allow it to be set by players independently.")
    var partyPvp = PartyPvpSetting.SET_BY_PLAYER

    @JvmField @Nest @Expanded var foodSettings = FoodSettings()

    @SectionHeader("miscellaneous")

    @JvmField @Nest @Expanded var soundSettings = SoundSettings()

    data class FoodSettings(
        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("Configure the delay for consuming food items (vanilla: 65 ticks).")
        var cooldown: Int = 0,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("A scale that is factored into the final cooldown: [ cooldown += cooldownDifficultyScale * difficulty ]")
        var cooldownDifficultyScale: Int = 0,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("Forces the cooldown even when you are not full.")
        var forceCooldown: Boolean = false,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("Tick duration when eating normal foods.")
        var foodTime: Int = 32,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("Tick duration when eating snack/fast foods.")
        var snackTime: Int = 16
    )

    data class SoundSettings(
        @JvmField
        @Sync(SyncMode.NONE)
        @RangeConstraint(min = 0.0, max = 100.0)
        @Comment("Adjusting the volume for the PVP sounds.")
        var pvpSoundVolume: Int = 100
    )

    enum class PartyPvpSetting {
        ENABLED,
        DISABLED,
        SET_BY_PLAYER
    }
}