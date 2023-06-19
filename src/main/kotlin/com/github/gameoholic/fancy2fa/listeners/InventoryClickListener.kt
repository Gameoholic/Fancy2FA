package com.github.gameoholic.fancy2fa.listeners

import com.github.gameoholic.fancy2fa.Fancy2FA
import com.github.gameoholic.fancy2fa.managers.MenuManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object InventoryClickListener : Listener {


        @EventHandler
        fun onInventoryClick(e: InventoryClickEvent) {
                val player = e.whoClicked as Player

                if (Fancy2FA.unverifiedPlayers.contains(player.uniqueId))
                        e.isCancelled = true

                if (!Fancy2FA.playerStates.containsKey(player.uniqueId)) return

                val itemStack = e.currentItem ?: return

                MenuManager.handleMenuItemClick(player, itemStack, Fancy2FA.playerStates[player.uniqueId]!!.type, e.slot)

                e.isCancelled = true
        }


}