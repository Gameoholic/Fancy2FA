package com.github.gameoholic.fancy2fa.listeners

import com.github.gameoholic.fancy2fa.Fancy2FA
import com.github.gameoholic.fancy2fa.datatypes.*
import com.github.gameoholic.fancy2fa.managers.ConfigManager
import com.github.gameoholic.fancy2fa.utils.DBUtil
import com.github.gameoholic.fancy2fa.managers.MenuManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object LoginListener : Listener {

        @EventHandler
        fun onPlayerJoin(e: PlayerJoinEvent) {
                Fancy2FA.packetManager.injectPlayer(e.player)
                //todo: add support for unforced players
                val isPlayerForced: Boolean = ConfigManager.forcedPlayers.contains(e.player.uniqueId)
                if (!isPlayerForced) return


                val hasSufficient2FAQuery: DBResult<Boolean>? = DBUtil.runDBOperation(DBUtil.hasSufficient2FA(e.player.uniqueId))

                if (hasSufficient2FAQuery == null) {
                        //todo: log
                        e.player.kick(Component.text(
                                "An internal error occurred within 2FA. Please contact your server administrator.")
                                .color(NamedTextColor.RED))
                        return
                }

                //todo: even players who need to create credentials need to be frozen
                if (!hasSufficient2FAQuery.result) {
                        e.player.sendMessage("Please create credentials.")
                        MenuManager.displayInfoMenu(e.player)
                        return
                }

                val securityQuestions: MutableList<SecurityQuestion> =
                        (DBUtil.runDBOperation(DBUtil.getPlayerSecurityQuestions(e.player.uniqueId)) ?: return).result
                val authData: PlayerPasswordData? =
                        (DBUtil.runDBOperation(DBUtil.getPlayerPasswordData(e.player.uniqueId)) ?: return).result
                val discordAuthData: DiscordAuthData? =
                        (DBUtil.runDBOperation(DBUtil.getPlayerDiscordAuthData(e.player.uniqueId)) ?: return).result

                //Must run on main thread
                Fancy2FA.plugin.server.scheduler.runTask(Fancy2FA.plugin, Runnable {
                        val fullAuthData = Player2FALoginData(0, securityQuestions, authData, discordAuthData)
                        MenuManager.displayVerificationMenu(e.player, fullAuthData)
                })

                Fancy2FA.unverifiedPlayers.put(e.player.uniqueId, e.player.location)



        }





}



