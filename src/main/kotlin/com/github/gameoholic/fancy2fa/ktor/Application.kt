package com.github.gameoholic.fancy2fa.ktor

import com.github.gameoholic.fancy2fa.Fancy2FA
import com.github.gameoholic.fancy2fa.discord.DiscordAutentication
import com.github.gameoholic.fancy2fa.managers.ConfigManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun main() {
        println(DiscordAutentication.generateAuthLink("asd"))
        GlobalScope.launch {
                embeddedServer(Netty, port = ConfigManager.discordWebserverPort) {
                        routing {
                                get("/auth/callback") {
                                        val params = call.request.queryParameters
                                        val code: String? = params["code"]
                                        val state: String? = params["state"]
                                        if (code != null && state != null) {
                                                val playerUUID = Fancy2FA.discordAuthStates[state]
                                                if (playerUUID == null)
                                                        call.respond(
                                                                HttpStatusCode.BadRequest,
                                                                "Invalid state provided."
                                                        )
                                                else {
                                                        call.respond(
                                                                HttpStatusCode.OK,
                                                                "You have been authenticated.\nYou may now close this page. Or not, I won't judge you."
                                                        )
                                                        DiscordAutentication.getUserAccessToken(code, playerUUID)
                                                }

                                        } else {
                                                call.respond(
                                                        HttpStatusCode.BadRequest,
                                                        "Authorization code or state not found"
                                                )
                                        }
                                }
                        }
                }.start(wait = true)
        }


}

