package com.bms.alboe.extras

import com.bibireden.playerex.api.attribute.PlayerEXAttributes
import com.bibireden.playerex.ext.component
import com.bibireden.playerex.ext.level
import com.bms.alboe.extras.screen.PartyMemberHud
import com.bms.alboe.extras.messaging.MessageDispatcher
import com.bms.alboe.extras.networking.AlboeExtrasNetworking
import com.bms.alboe.extras.networking.events.NetworkingEventPvp
import com.bms.alboe.extras.sounds.AlboeExtrasSounds
import com.hypherionmc.simplerpc.api.variables.PlaceholderEngine
import com.mojang.blaze3d.platform.InputConstants
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.sounds.SoundSource
import org.lwjgl.glfw.GLFW

object AlboeExtrasClient : ClientModInitializer {
	@JvmField
	val KEYBINDING_PARTY_HUD: KeyMapping = KeyBindingHelper.registerKeyBinding(KeyMapping("${AlboeExtras.MOD_ID}.key.party_member_hud", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_TAB, "key.categories.${AlboeExtras.MOD_ID}"))

	override fun onInitializeClient() {
		PartyMemberHud.registerNetworkingHandlers()

		ClientLifecycleEvents.CLIENT_STARTED.register { minecraft ->
			PlaceholderEngine.INSTANCE.registerPlaceholder("playerex.level", "Level") {
				minecraft.player?.component?.get(PlayerEXAttributes.LEVEL)?.toInt()?.toString() ?: "unknown"
			}

			PlaceholderEngine.INSTANCE.registerPlaceholder("playerex.item_level", "Item Level") {
				val items = minecraft.player?.inventory?.items ?: return@registerPlaceholder "0"
				var topLevel = 0

				items.forEach { stack -> if (topLevel < stack.level) topLevel = stack.level }
				topLevel.toString()
			}

			ClientTickEvents.END_CLIENT_TICK.register { PartyMemberHud.tick(minecraft) }
		}

		AlboeExtrasNetworking.EVENTS.registerClientbound(NetworkingEventPvp::class.java) { (playerName, enabled), access ->
			// message out
			(if (enabled) MessageDispatcher.partyPvpEnabledPlayer(playerName) else MessageDispatcher.partyPvpDisabledPlayer(playerName)).sendLocal(access.player())
			access.player().playNotifySound(if (enabled) AlboeExtrasSounds.PVP_ENABLED else AlboeExtrasSounds.PVP_DISABLED, SoundSource.NEUTRAL, AlboeExtras.CONFIG.soundSettings.pvpSoundVolume.toFloat(), 1F)
		}
	}
}