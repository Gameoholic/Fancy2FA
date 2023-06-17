package com.github.gameoholic.fancy2fa.managers

import com.github.gameoholic.fancy2fa.Fancy2FA
import java.io.File
import java.util.*

object ConfigManager {

        val forcedPlayers : MutableList<UUID> = mutableListOf()
        val securityQuestions: MutableList<String> = mutableListOf()
        var hashLogRounds = 0
        var generalPepper = ""
        var discordPepper = ""
        var questionsRequired = 0
        var answerQuestionsRequired = 0
        var SQLUsername = ""
        var SQLPassword = ""
        var discordClientID = ""
        var discordClientSecret = ""
        var discordWebserverPort = 0
        var SQLName = ""
        var SQLPort = 0
        var authenticationIPCooldown = 0

        fun loadConfig() {
                Fancy2FA.instance?.config?.getStringList("ForceUsers")?.forEach { forcedUserUUID ->
                        forcedPlayers.add(UUID.fromString(forcedUserUUID))
                }
                loadSecurityQuestions()
                hashLogRounds = Fancy2FA.instance?.config?.getInt("HashLogRounds")!!
                generalPepper = Fancy2FA.instance?.config?.getString("Pepper")!!
                discordPepper = Fancy2FA.instance?.config?.getString("DiscordPepper")!!
                questionsRequired = Fancy2FA.instance?.config?.getInt("MinQuestions")!!
                answerQuestionsRequired = Fancy2FA.instance?.config?.getInt("AnswerQuestions")!!

                authenticationIPCooldown = Fancy2FA.instance?.config?.getInt("AuthenticationIPCooldown")!!

                SQLPort = Fancy2FA.instance?.config?.getInt("SQLPort")!!
                SQLName = Fancy2FA.instance?.config?.getString("SQLName")!!
                SQLUsername = Fancy2FA.instance?.config?.getString("SQLUsername")!!
                SQLPassword = Fancy2FA.instance?.config?.getString("SQLPassword")!!

                discordClientID = Fancy2FA.instance?.config?.getString("DiscordClientID")!!
                discordClientSecret = Fancy2FA.instance?.config?.getString("DiscordClientSecret")!!
                discordWebserverPort = Fancy2FA.instance?.config?.getInt("DiscordWebserverPort")!!

        }

        private fun loadSecurityQuestions() {
                val file = File(Fancy2FA.instance?.dataFolder, "security_questions.txt")
                file.forEachLine { line ->
                        securityQuestions.add(line)
                }
        }
}