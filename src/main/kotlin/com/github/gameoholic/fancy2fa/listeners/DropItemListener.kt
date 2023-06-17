package com.github.gameoholic.fancy2fa.listeners

import com.github.gameoholic.fancy2fa.Fancy2FA
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

object DropItemListener : Listener {

        @EventHandler
        fun onDropItemMessage(e: PlayerDropItemEvent) {
                if (Fancy2FA.instance?.unverifiedPlayers?.contains(e.player.uniqueId)!!)
                        e.isCancelled = true
        }
}