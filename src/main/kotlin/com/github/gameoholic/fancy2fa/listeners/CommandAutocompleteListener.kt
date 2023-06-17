package com.github.gameoholic.fancy2fa.listeners

import com.github.gameoholic.fancy2fa.Fancy2FA
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandSendEvent


object CommandAutocompleteListener : Listener {

        @EventHandler
        fun onPlayerTab(e: PlayerCommandSendEvent) {
                if (Fancy2FA.instance?.unverifiedPlayers?.contains(e.player.uniqueId)!!)
                        e.commands.clear()
        }
}
