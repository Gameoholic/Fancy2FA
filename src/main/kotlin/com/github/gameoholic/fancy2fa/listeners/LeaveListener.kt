package com.github.gameoholic.fancy2fa.listeners

import com.github.gameoholic.fancy2fa.Fancy2FA
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object LeaveListener : Listener {

        @EventHandler
        fun onPlayerLeave(e: PlayerQuitEvent) {
                Fancy2FA?.instance?.packetManager?.removePlayer(e.player)
                Fancy2FA?.instance?.playerState?.remove(e.player.uniqueId)
                Fancy2FA?.instance?.unverifiedPlayers?.remove(e.player.uniqueId)
        }



}