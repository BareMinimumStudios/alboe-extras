package com.bms.alboe.extras.state

import dev.onyxstudios.cca.api.v3.component.Component

interface IPlayerData : Component {
    val isPartyPvpEnabled: Boolean

    fun setPartyPvpEnabled(isPartyPvp: Boolean)
}