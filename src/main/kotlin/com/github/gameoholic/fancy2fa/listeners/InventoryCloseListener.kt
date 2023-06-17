package com.github.gameoholic.fancy2fa.listeners

import com.github.gameoholic.fancy2fa.Fancy2FA
import com.github.gameoholic.fancy2fa.datatypes.DBResult
import com.github.gameoholic.fancy2fa.datatypes.PlayerStateType
import com.github.gameoholic.fancy2fa.managers.DBManager
import com.github.gameoholic.fancy2fa.managers.PromptManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import java.util.*

object InventoryCloseListener : Listener {

        @EventHandler
        fun onInventoryClose(e: InventoryCloseEvent) {
                val player = e.player as Player

                if (!Fancy2FA.instance?.playerState?.containsKey(player.uniqueId)!!) return


                //We don't want to display an error menu to the player. In case it results in a DB error, it will do an infinite loop of showing
                //the error menu. So we don't pass the player to DBManager#runDBOperation()
                val allowedToCloseMenuQuery: DBResult<Boolean>? = DBManager.runDBOperation(DBManager.hasSufficient2FA(player.uniqueId))
                if (allowedToCloseMenuQuery == null) {
                        player.sendMessage(Component.text(
                                "An internal error occurred within 2FA. Please contact your server administrator.")
                                .color(NamedTextColor.RED))
                        //todo: log
                        return
                }

                val isBeingVerified: Boolean = Fancy2FA.instance!!.unverifiedPlayers.contains(player.uniqueId)

                if (e.reason != InventoryCloseEvent.Reason.PLAYER) return
                if (allowedToCloseMenuQuery.result && !isBeingVerified) {
                        player.sendMessage(Component.text(
                                "Successfully saved 2FA configuration.")
                                .color(NamedTextColor.GREEN))
                        Fancy2FA.instance?.playerState!!.remove(player.uniqueId)
                }
                else {
                        //If inventory open threw an exception or did not open as expected and was set to null as a result,
                        // or the player is not in an inventory menu, return
                        val menuInventory: Inventory = Fancy2FA.instance?.playerState?.get(player.uniqueId)?.menuInventory ?: return

                        //Reopen inventory menu
                        Fancy2FA.instance!!.server.scheduler.runTask(Fancy2FA.instance!!, Runnable {
                                player.openInventory(menuInventory)
                        })
                }
        }


}