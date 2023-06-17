package com.github.gameoholic.fancy2fa.listeners

import com.github.gameoholic.fancy2fa.Fancy2FA
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ChatMessageListener : Listener {

        @EventHandler
        fun onPlayerChatMessage(e: AsyncChatEvent) {
                if (Fancy2FA.instance?.unverifiedPlayers?.contains(e.player.uniqueId)!!)
                       e.isCancelled = true
        }
}
