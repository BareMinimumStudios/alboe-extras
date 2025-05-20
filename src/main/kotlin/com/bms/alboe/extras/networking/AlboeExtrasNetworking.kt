package com.bms.alboe.extras.networking

import com.bms.alboe.extras.AlboeExtras
import io.wispforest.owo.network.OwoNetChannel

object AlboeExtrasNetworking {
    val PARTY_MEMBERS = OwoNetChannel.create(AlboeExtras.id("party_members"))

    val EVENTS = OwoNetChannel.create(AlboeExtras.id("events"))
}