package com.github.gameoholic.fancy2fa.datatypes

import java.util.*

data class SecurityQuestion(val question: String, val answerHash: String, val answerSalt: String)