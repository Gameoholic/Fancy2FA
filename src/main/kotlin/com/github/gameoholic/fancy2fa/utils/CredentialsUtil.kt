package com.github.gameoholic.fancy2fa.utils

import org.mindrot.jbcrypt.BCrypt
import java.util.*

object CredentialsUtil {

        //Used to create credential
        fun genSalt(logRounds: Int): String {
                return BCrypt.gensalt(logRounds)
        }

        fun hashString(string: String, salt: String, pepper: String): String {
                return BCrypt.hashpw(string.lowercase(Locale.getDefault()) + pepper, salt)
        }





}