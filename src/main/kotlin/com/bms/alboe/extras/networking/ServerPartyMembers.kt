package com.bms.alboe.extras.networking

import java.util.UUID

@JvmRecord
data class ServerPartyMembers(val data: MutableMap<UUID, Entry>) {
    @JvmRecord
    data class Entry(
        val health: Float,
        val maxHealth: Float,
        val isPartyLeader: Boolean
    )
}