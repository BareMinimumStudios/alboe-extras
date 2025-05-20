package com.bms.alboe.extras.screen

import com.bms.alboe.extras.AlboeExtras
import com.bms.alboe.extras.AlboeExtrasClient
import com.bms.alboe.extras.networking.AlboeExtrasNetworking
import com.bms.alboe.extras.networking.ClientPartyMembers
import com.bms.alboe.extras.networking.ServerPartyMembers
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import io.wispforest.owo.ui.hud.Hud
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.PlayerInfo
import xaero.pac.client.api.OpenPACClientAPI
import java.util.UUID
import kotlin.reflect.KClass
import net.minecraft.network.chat.Component as Text

// Transformers
fun <T : Component> ParentComponent.childById(clazz: KClass<T>, id: String) = this.childById(clazz.java, id)

object PartyMemberHud {
    private val id = AlboeExtras.id("party_member_hud")

    private var playersToData = mutableMapOf<UUID, ServerPartyMembers.Entry>()

    fun registerNetworkingHandlers() {
        AlboeExtrasNetworking.PARTY_MEMBERS.registerClientbound(ServerPartyMembers::class.java) { members, access ->
            playersToData = members.data
            // signal update
            onUpdate(access.runtime())
        }
    }

    private fun updateOrCreateHudElement(hud: FlowLayout, info: PlayerInfo) {
        val data = playersToData[info.profile.id] ?: return
        val area = hud.childById(LabelComponent::class, info.profile.id.toString()) ?: createHudElement(hud, info)

        area.text(Text.literal(if (data.isPartyLeader) "⚑ " else "").append(Text.literal("${info.profile.name} >> ❤ ${((data.health / data.maxHealth) * 100).toInt()}%")))
    }

    private fun onUpdate(client: Minecraft) {
        Hud.getComponent(id)?.let {
            val hud = it as FlowLayout

            client.connection?.onlinePlayers?.forEach { info -> updateOrCreateHudElement(hud, info) }
        }
    }

    private fun createHudElement(hud: FlowLayout, info: PlayerInfo): LabelComponent {
        val area = Components.label(Text.literal("waiting..."))
        hud.child(area.id(info.profile.id.toString()))
        return area
    }

    fun tick(client: Minecraft) {
        if (AlboeExtrasClient.KEYBINDING_PARTY_HUD.isDown) {
            if (!Hud.hasComponent(id)) {
                Hud.add(id) {
                    Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .padding(Insets.of(10))
                        .surface(Surface.DARK_PANEL)
                        .verticalAlignment(VerticalAlignment.TOP)
                        .horizontalAlignment(HorizontalAlignment.CENTER)
                        .positioning(Positioning.relative(1, 25))
                }
            }
        }
        else {
           if (Hud.hasComponent(id)) Hud.remove(id)
        }

        OpenPACClientAPI.get().clientPartyStorage.party?.let { party -> AlboeExtrasNetworking.PARTY_MEMBERS.clientHandle().send(ClientPartyMembers(party.id)) }
    }
}