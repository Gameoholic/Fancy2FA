package com.github.gameoholic.fancy2fa.listeners

import com.github.gameoholic.fancy2fa.Fancy2FA
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

object InteractListener : Listener {

        @EventHandler
        fun onPlayerChatMessage(e: PlayerInteractEvent) {
                if (Fancy2FA.unverifiedPlayers.contains(e.player.uniqueId))
                        e.isCancelled = true
        }
}
