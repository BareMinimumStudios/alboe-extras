package com.bms.alboe.extras

import com.bibireden.playerex.networking.registerServerbound
import com.bms.alboe.extras.config.AlboeExtrasConfig
import com.bms.alboe.extras.networking.AlboeExtrasNetworking
import com.bms.alboe.extras.networking.ClientPartyMembers
import com.bms.alboe.extras.networking.events.NetworkingEventPvp
import com.bms.alboe.extras.networking.ServerPartyMembers
import io.wispforest.owo.network.serialization.PacketBufSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.world.level.GameRules
import org.slf4j.LoggerFactory
import toni.immersivemessages.api.ImmersiveMessage
import toni.immersivemessages.util.ImmersiveColor
import xaero.pac.common.server.api.OpenPACServerAPI
import xaero.pac.common.server.parties.party.api.IPartyManagerAPI
import java.util.*

fun ImmersiveMessage.alboe(): ImmersiveMessage {
	return this.backgroundColor(ImmersiveColor.BLACK).borderTopColor(ImmersiveColor.BLACK).borderBottomColor(ImmersiveColor.BLACK)
}

object AlboeExtras : ModInitializer {
	const val MOD_ID = "alboe-extras"

	fun id(path: String): ResourceLocation = ResourceLocation.tryBuild(MOD_ID, path)!!

	@JvmField
	val LOGGER = LoggerFactory.getLogger("alboe-extras")

	@JvmField
	val CONFIG = AlboeExtrasConfig.createAndLoad()

	@JvmField
	var SERVER_PARTY_MANAGER: IPartyManagerAPI? = null

	private val FIRST_JOIN_TOAST_MESSAGE = ImmersiveMessage.toast(30F, "Welcome to the world of ALBOE!", "Open your quest book to get started.")
		.fadeIn(2F).fadeOut(2F)
		.typewriter(1.5F, false)
		.alboe()

	private fun isFirstTimePlaying(player: ServerPlayer): Boolean {
		return player.stats.getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0
	}

	override fun onInitialize() {
		AlboeExtrasNetworking.PARTY_MEMBERS.registerClientboundDeferred(ServerPartyMembers::class.java)
		AlboeExtrasNetworking.EVENTS.registerClientboundDeferred(NetworkingEventPvp::class.java)

		AlboeExtrasNetworking.PARTY_MEMBERS.registerServerbound(ClientPartyMembers::class.java) { (uuid), access ->
			val api = SERVER_PARTY_MANAGER ?: return@registerServerbound

			val caller = access.player
			val listing = caller.server.playerList

			val party = api.getPartyById(uuid) ?: return@registerServerbound

			// check if player is in the party
			if (party.getMemberInfo(caller.uuid) == null) return@registerServerbound

			AlboeExtrasNetworking.PARTY_MEMBERS.serverHandle(access.player).send(
				ServerPartyMembers(party.memberInfoStream.toList().mapNotNull { listing.getPlayer(it.uuid) }.associate { it.uuid to ServerPartyMembers.Entry(it.health, it.maxHealth, party.owner.uuid == it.uuid) }.toMutableMap())
			)
		}

		ServerPlayConnectionEvents.INIT.register { instance, _ ->
			this.SERVER_PARTY_MANAGER = Objects.requireNonNullElseGet(SERVER_PARTY_MANAGER) { OpenPACServerAPI.get(instance.player.server).partyManager }

			instance.player.level().gameRules.getRule(GameRules.RULE_KEEPINVENTORY).set(true, instance.player.server)
		}

		ServerPlayConnectionEvents.JOIN.register { listener, _, server ->
			if (this.isFirstTimePlaying(listener.player)) {
				FIRST_JOIN_TOAST_MESSAGE.sendServer(listener.player)
			}
		}

		CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
			dispatcher.register(Commands.literal("alboe").then(Commands.literal("pvp").executes { stack ->
				stack.source.player?.let { player ->
					val data = player.getComponent(Components.PLAYER_DATA)

					data.setPartyPvpEnabled(!data.isPartyPvpEnabled)

					stack.source.sendSuccess({ Component.translatable(if (data.isPartyPvpEnabled) "alboe_extras.parties.pvp.enabled" else "alboe_extras.parties.pvp.disabled") }, false)

					// obtain player(s)
					val party = SERVER_PARTY_MANAGER!!.getPartyByMember(player.uuid)


					if (party != null) {
						AlboeExtrasNetworking.EVENTS.serverHandle(party.onlineMemberStream.toList()).send(NetworkingEventPvp(data.isPartyPvpEnabled))
					}

					1
				}
				-1
			}))
		}
	}
}