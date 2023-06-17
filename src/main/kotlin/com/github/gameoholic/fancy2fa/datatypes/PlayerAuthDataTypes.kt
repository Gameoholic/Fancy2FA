package com.github.gameoholic.fancy2fa.datatypes

import java.util.UUID

data class PlayerPasswordData(val uuid: UUID, val password: String, val salt: String)
data class DiscordAuthData(val idHash: String, val idSalt: String, var username: String)
data class Player2FALoginData(var currentQuestion: Int, val securityQuestions: MutableList<SecurityQuestion>, val passwordData: PlayerPasswordData?, val discordAuthData: DiscordAuthData?)
