package com.bms.alboe.extras

import com.bms.alboe.extras.state.IPlayerData
import com.bms.alboe.extras.state.PlayerData
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy
import net.minecraft.resources.ResourceLocation

class Components : EntityComponentInitializer {
    companion object {
        @JvmField
        val PLAYER_DATA: ComponentKey<IPlayerData> = ComponentRegistry.getOrCreate(ResourceLocation.tryBuild(AlboeExtras.MOD_ID, "player-data")!!, IPlayerData::class.java)
    }

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerForPlayers(PLAYER_DATA, ::PlayerData, RespawnCopyStrategy.ALWAYS_COPY)
    }
}