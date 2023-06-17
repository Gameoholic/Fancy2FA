package com.github.gameoholic.fancy2fa.managers

import org.bukkit.entity.Player
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import java.util.*

object CredentialsManager {

        //Used to create credential
        fun genSalt(logRounds: Int): String {
                return BCrypt.gensalt(logRounds)
        }

        fun hashString(string: String, salt: String, pepper: String): String {
                return BCrypt.hashpw(string.lowercase(Locale.getDefault()) + pepper, salt)
        }





}