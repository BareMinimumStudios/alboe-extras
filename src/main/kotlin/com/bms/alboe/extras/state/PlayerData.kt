package com.bms.alboe.extras.state

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player

class PlayerData(private val player: Player, private var _isPartyPvpEnabled: Boolean = false) : IPlayerData {
    override val isPartyPvpEnabled: Boolean
        get() = _isPartyPvpEnabled

    override fun setPartyPvpEnabled(isPartyPvp: Boolean) {
        this._isPartyPvpEnabled = isPartyPvp
    }

    override fun readFromNbt(tag: CompoundTag) {
        this._isPartyPvpEnabled = tag.getBoolean("IsPartyPvpEnabled")
    }

    override fun writeToNbt(tag: CompoundTag) {
        tag.putBoolean("IsPartyPvpEnabled", this.isPartyPvpEnabled)
    }
}