package com.bms.alboe.extras

import net.minecraft.server.level.ServerPlayer
import xaero.pac.common.server.parties.party.api.IPartyManagerAPI

fun isPlayersWithinSameParty(manager: IPartyManagerAPI, player1: ServerPlayer, player2: ServerPlayer): Boolean {
    val party1 = manager.getPartyByMember(player1.uuid)
    val party2 = manager.getPartyByMember(player2.uuid)
    return party1 != null && party2 != null && (party1 == party2)
}

fun isPartyPvpEnabledForBoth(player1: ServerPlayer, player2: ServerPlayer): Boolean {
    return player1.getComponent(Components.PLAYER_DATA).isPartyPvpEnabled && player2.getComponent(Components.PLAYER_DATA).isPartyPvpEnabled
}