package com.bms.alboe.extras

import com.bms.alboe.extras.config.AlboeExtrasConfig
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import org.slf4j.LoggerFactory
import toni.immersivemessages.api.ImmersiveMessage
import toni.immersivemessages.util.ImmersiveColor

fun ImmersiveMessage.alboe(): ImmersiveMessage {
	return this.backgroundColor(ImmersiveColor.BLACK).borderTopColor(ImmersiveColor.BLACK).borderBottomColor(ImmersiveColor.BLACK)
}

object AlboeExtras : ModInitializer {
	const val MOD_ID = "alboe-extras"

	@JvmField
	val CONFIG = AlboeExtrasConfig.createAndLoad()

    val logger = LoggerFactory.getLogger("alboe-extras")

	private val FIRST_JOIN_TOAST_MESSAGE = ImmersiveMessage.toast(30F, "Welcome to the world of ALBOE!", "Open your quest book to get started.")
		.typewriter(1.5F, false)
		.fadeIn(0.5F).fadeOut(0.5F)
		.alboe()

	private fun isFirstTimePlaying(player: ServerPlayer): Boolean {
		return player.stats.getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0
	}

	override fun onInitialize() {
		ServerPlayConnectionEvents.JOIN.register { listener, _, server ->
			if (this.isFirstTimePlaying(listener.player)) {
				FIRST_JOIN_TOAST_MESSAGE.sendServer(listener.player)
			}
		}

		CommandRegistrationCallback.EVENT.register { dispatcher, ctx, _ ->
			dispatcher.register(Commands.literal("alboe").then(Commands.literal("pvp").executes { stack ->
				stack.source.player?.getComponent(Components.PLAYER_DATA)?.let {
					it.setPartyPvpEnabled(!it.isPartyPvpEnabled)

					val translatable = if (it.isPartyPvpEnabled) "alboe_extras.parties.pvp.enabled" else "alboe_extras.parties.pvp.disabled"
					stack.source.sendSuccess({ Component.translatable(translatable) }, true)

					1
				}
				-1
			}))
		}
	}
}