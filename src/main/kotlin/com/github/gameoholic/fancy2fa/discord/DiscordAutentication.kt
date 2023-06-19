package com.github.gameoholic.fancy2fa.discord

import com.github.gameoholic.fancy2fa.Fancy2FA
import com.github.gameoholic.fancy2fa.datatypes.DiscordAuthData
import com.github.gameoholic.fancy2fa.datatypes.PlayerStateType
import com.github.gameoholic.fancy2fa.managers.ConfigManager
import com.github.gameoholic.fancy2fa.utils.CredentialsUtil
import com.github.gameoholic.fancy2fa.utils.DBUtil
import com.github.gameoholic.fancy2fa.managers.MenuManager
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.json.JSONObject
import java.net.InetAddress
import java.util.*


object DiscordAutentication {

        fun generateAuthLink(state: String): String {
                val clientID = ConfigManager.discordClientID
                val serverIP = InetAddress.getLocalHost().hostAddress.replace("/", "")
                val redirectURIEncoded = "http%3A%2F%2F${serverIP}%3A${ConfigManager.discordWebserverPort}%2Fauth%2Fcallback"

                var link = "https://discord.com/oauth2/authorize?"
                link += "response_type=code&"
                link += "client_id=$clientID&"
                link += "scope=identify&"
                link += "state=$state&"
                link += "redirect_uri=$redirectURIEncoded&"
                link += "prompt=consent"

                return link
        }


        suspend fun getUserAccessToken(code: String, playerUUID: UUID) {
                val clientID = ConfigManager.discordClientID
                val clientSecret = ConfigManager.discordClientSecret
                val serverIP = InetAddress.getLocalHost().hostAddress.replace("/", "")
                val redirectURI = "http://${serverIP}:${ConfigManager.discordWebserverPort}/auth/callback"

                val client = HttpClient(CIO)

                val response: HttpResponse = client.submitForm(
                        url = "https://discord.com/api/v10/oauth2/token",
                        formParameters = parameters {
                                append("client_id", clientID)
                                append("client_secret", clientSecret)
                                append("grant_type", "authorization_code")
                                append("code", code)
                                append("redirect_uri", redirectURI)
                        }
                )

                val json = JSONObject(response.bodyAsText())
                val accessToken = json.get("access_token").toString()
                val refreshToken = json.get("refresh_token").toString()

                getUserData(accessToken, client, playerUUID)
                revokeToken(accessToken, "access_token", client)
                revokeToken(refreshToken, "refresh_token", client)

                client.close()
        }

        private suspend fun revokeToken(token: String, tokenTypeHint: String, client: HttpClient) {
                val clientID = ConfigManager.discordClientID
                val clientSecret = ConfigManager.discordClientSecret

                client.submitForm(
                        url = "https://discord.com/api/v10/oauth2/token/revoke",
                        formParameters = parameters {
                                append("client_id", clientID)
                                append("client_secret", clientSecret)
                                append("token", token)
                                append("token_type_hint", tokenTypeHint)
                        }
                )
        }

        private suspend fun getUserData(accessToken: String, client: HttpClient, playerUUID: UUID){
                Fancy2FA.discordAuthStates.remove<Any, UUID>(playerUUID)
                val response: HttpResponse = client.get("https://discordapp.com/api/users/@me") {
                        headers {
                                append(HttpHeaders.Authorization, "Bearer $accessToken")
                        }
                }

                val json = JSONObject(response.bodyAsText())

                val userID = json.get("id").toString()
                val username = json.get("username").toString()

                val verification: Boolean = Fancy2FA.playerStates[playerUUID]?.type!! == PlayerStateType.VERIFICATION_DISCORD_PROMPT

                val player = Bukkit.getPlayer(playerUUID) ?: return

                if (verification) {
                        val discordData : DiscordAuthData? = (DBUtil.runDBOperation(
                                DBUtil.getPlayerDiscordAuthData(playerUUID), player) ?: return).result
                        if (discordData == null ||
                                CredentialsUtil.hashString(userID, discordData.idSalt, ConfigManager.discordPepper) != discordData.idHash) {
                                Fancy2FA.plugin.server.scheduler.runTask(Fancy2FA.plugin, Runnable {
                                        MenuManager.displayErrorMenu(player, "Failed to verify your Discord account.")
                                })
                                return
                        }
                        Fancy2FA.unverifiedPlayers.remove(playerUUID)
                        Bukkit.getPlayer(playerUUID)?.let {
                                Fancy2FA.plugin.server.scheduler.runTask(Fancy2FA.plugin, Runnable {
                                        it.closeInventory()
                                })
                        }
                        Bukkit.getPlayer(playerUUID)?.sendMessage(Component.text("Successfully authenticated!").color(NamedTextColor.GREEN))
                        Fancy2FA.unverifiedPlayers.remove(player.uniqueId)
                        Fancy2FA.playerStates.remove(playerUUID)
                }
                else {
                        DBUtil.runDBOperation(DBUtil.setDiscordAuth(playerUUID, userID, username), player)
                        //Must run on main thread
                        Bukkit.getPlayer(playerUUID)?.let {
                                Fancy2FA.plugin.server.scheduler.runTask(Fancy2FA.plugin, Runnable {
                                        MenuManager.displayMainMenu(it)
                                })
                        }
                }
        }





}