package com.github.gameoholic.fancy2fa.managers

import com.github.gameoholic.fancy2fa.Fancy2FA
import com.github.gameoholic.fancy2fa.datatypes.PlayerPasswordData
import com.github.gameoholic.fancy2fa.datatypes.PlayerState
import com.github.gameoholic.fancy2fa.datatypes.PlayerStateType
import com.github.gameoholic.fancy2fa.datatypes.SecurityQuestion
import com.github.gameoholic.fancy2fa.discord.DiscordAutentication
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.*
import java.util.*

object PromptManager {
        fun promptQuestionAnswer(player: Player, question: String) {
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.ANSWER_QUESTION_PROMPT, question))
                Fancy2FA.instance?.packetManager?.createSendFakeSign()?.sendFakeSignPacket(
                        player, "", "^^^^^^^^^^^^^^^", "Answer here")
        }
        private fun handleQuestionAnswerPrompt(player: Player, answer: String) {
                if (answer.isEmpty()) {
                        MenuManager.displayErrorMenu(player, "The answer mustn't be empty.")
                        return
                }
                val question: String = Fancy2FA.instance!!.playerState?.get(player.uniqueId)?.data.toString()
                DBManager.runDBOperation(DBManager.addSecurityQuestion(question, answer, player.uniqueId), player)
                player.sendMessage("Successfully created $answer for $question")
                MenuManager.displayMainMenu(player)
        }

        fun promptQuestionUpdate(player: Player, question: String) {
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.UPDATE_QUESTION_PROMPT, question))
                Fancy2FA.instance?.packetManager?.createSendFakeSign()?.sendFakeSignPacket(
                        player, "", "^^^^^^^^^^^^^^^", "New answer here")
        }

        private fun handleQuestionUpdatePrompt(player: Player, answer: String) {
                val question: String = Fancy2FA.instance!!.playerState?.get(player.uniqueId)?.data.toString()
                DBManager.runDBOperation(DBManager.updateSecurityQuestion(question, answer, player.uniqueId), player)

                player.sendMessage("Successfully updated $answer for $question")
                MenuManager.displayMainMenu(player)
        }

        fun promptCreateCustomQuestion(player: Player) {
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.CREATE_CUSTOM_QUESTION_PROMPT))
                Fancy2FA.instance?.packetManager?.createSendFakeSign()?.sendFakeSignPacket(
                        player, "", "", "^Question here^", "Can be multi. lines")
        }

        private fun handleCreateCustomQuestionPrompt(player: Player, question: String) {
                val securityQuestionExists: Boolean =
                        (DBManager.runDBOperation(DBManager.doesSecurityQuestionExist(question, player.uniqueId), player) ?: return).result
                if (securityQuestionExists) {
                        MenuManager.displayErrorMenu(player, "You've already created this security question, please pick another one.")
                        return
                }
                if (question == "") {
                        MenuManager.displayErrorMenu(player, "The security question mustn't be empty.")
                        return
                }
                promptQuestionAnswer(player, question)
        }
        fun promptCreatePassword(player: Player) {
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.CREATE_PASSWORD_PROMPT))
                Fancy2FA.instance?.packetManager?.createSendFakeSign()?.sendFakeSignPacket(
                        player, "", "^^^^^^^^^^^^^^^", "Password here")
        }
        private fun handleCreatePasswordPrompt(player: Player, password: String) {
                if (password.length < 5) {
                        MenuManager.displayErrorMenu(player, "Your password must be at least 5 characters long.")
                        return
                }
                DBManager.setPassword(password, player.uniqueId)
                MenuManager.displayMainMenu(player)
        }
        fun promptUpdatePassword(player: Player) {
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.UPDATE_PASSWORD_PROMPT))
                Fancy2FA.instance?.packetManager?.createSendFakeSign()?.sendFakeSignPacket(
                        player, "", "^^^^^^^^^^^^^^^", "Password here")
        }
        private fun handleUpdatePasswordPrompt(player: Player, password: String) {
                if (password.length < 5) {
                        MenuManager.displayErrorMenu(player, "Your password must be at least 5 characters long.")
                        return
                }
                DBManager.setPassword(password, player.uniqueId)
                MenuManager.displayMainMenu(player)
        }

        fun promptEnterPassword(player: Player) {
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.VERIFICATION_PASSWORD_PROMPT))
                Fancy2FA.instance?.packetManager?.createSendFakeSign()?.sendFakeSignPacket(
                        player, "", "^^^^^^^^^^^^^^^", "Password here")
        }
        private fun handleEnterPasswordPrompt(player: Player, password: String) {
                //We can assume user has password set
                val passwordData: PlayerPasswordData =
                        (DBManager.runDBOperation(DBManager.getPlayerPasswordData(player.uniqueId), player) ?: return).result!!
                if (passwordData.password != CredentialsManager.hashString(password, passwordData.salt, ConfigManager.generalPepper)) {
                        MenuManager.displayErrorMenu(player, "Your password is incorrect.")
                        return
                }
                Bukkit.getPlayer(player.uniqueId)?.sendMessage(Component.text("Successfully authenticated!").color(NamedTextColor.GREEN))
                Fancy2FA.instance?.unverifiedPlayers?.remove(player.uniqueId)
                Fancy2FA.instance?.playerState!!.remove(player.uniqueId)
        }

        fun promptDiscordAuthProcess(player: Player, verification: Boolean = false) {
                //If state already exists (book was re-opened) re-use it
                var state: String = (Fancy2FA.instance?.discordAuthStates!!.get<Any, UUID>(player.uniqueId) ?: UUID.randomUUID()).toString()

                val link = DiscordAutentication.generateAuthLink(state)

                if (verification)
                        Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.VERIFICATION_DISCORD_PROMPT))
                else
                        Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.DISCORD_AUTH_PROMPT))

                Fancy2FA.instance?.discordAuthStates?.put(state, player.uniqueId)

                player.openBook(
                        Book.book(
                                Component.text("Discord Auth"),
                                Component.text("2FA"),
                                listOf(
                                        Component.text("Click here to authenticate yourself on ")
                                                .color(NamedTextColor.BLACK)
                                                .clickEvent(
                                                        ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, link)
                                                )
                                                .append(
                                                        Component.text("Discord")
                                                                .color(NamedTextColor.BLUE)
                                                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, link))
                                                ))
                        ))
        }

        fun promptEnterQuestionAnswer(player: Player, question: String) {
                val securityQuestions: MutableList<SecurityQuestion> =
                        (DBManager.runDBOperation(DBManager.getPlayerSecurityQuestions(player.uniqueId), player) ?: return).result!!
                //We assume security question exists in DB
                val securityQuestion = securityQuestions.filter { it.question == question }[0]
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.VERIFICATION_QUESTION_PROMPT, securityQuestion))
                Fancy2FA.instance?.packetManager?.createSendFakeSign()?.sendFakeSignPacket(
                        player, "", "^^^^^^^^^^^^^^^", "Answer here")
        }
        private fun handleEnterQuestionAnswerPrompt(player: Player, answer: String) {
                val securityQuestion = Fancy2FA?.instance?.playerState?.get(player.uniqueId)?.data as SecurityQuestion
                if (securityQuestion.answerHash != CredentialsManager.hashString(answer, securityQuestion.answerSalt, ConfigManager.generalPepper)) {
                        MenuManager.displayErrorMenu(player, "The answer is incorrect.")
                        return
                }
                Bukkit.getPlayer(player.uniqueId)?.sendMessage(Component.text("Successfully authenticated!").color(NamedTextColor.GREEN))
                Fancy2FA.instance?.unverifiedPlayers?.remove(player.uniqueId)
                Fancy2FA.instance?.playerState!!.remove(player.uniqueId)
        }

        //This method triggers whenever a sign update packet is sent to the server. Returns true whether should block packet
        fun onPlayerSignUpdate(player: Player, lines: Array<String>?): Boolean {
                when (Fancy2FA?.instance?.playerState?.get(player.uniqueId)?.type) {
                        PlayerStateType.ANSWER_QUESTION_PROMPT -> {
                                //Must run on main thread
                                Fancy2FA.instance!!.server.scheduler.runTask(Fancy2FA.instance!!, Runnable {
                                        handleQuestionAnswerPrompt(player, lines!![0])
                                })
                                return true
                        }
                        PlayerStateType.UPDATE_QUESTION_PROMPT -> {
                                //Must run on main thread
                                Fancy2FA.instance!!.server.scheduler.runTask(Fancy2FA.instance!!, Runnable {
                                        handleQuestionUpdatePrompt(player, lines!![0])
                                })
                                return true
                        }
                        PlayerStateType.CREATE_CUSTOM_QUESTION_PROMPT -> {
                                //Must run on main thread
                                Fancy2FA.instance!!.server.scheduler.runTask(Fancy2FA.instance!!, Runnable {
                                        handleCreateCustomQuestionPrompt(player, lines!![0] + lines!![1])
                                })
                                return true
                        }
                        PlayerStateType.CREATE_PASSWORD_PROMPT -> {
                                //Must run on main thread
                                Fancy2FA.instance!!.server.scheduler.runTask(Fancy2FA.instance!!, Runnable {
                                        handleCreatePasswordPrompt(player, lines!![0])
                                })
                                return true
                        }
                        PlayerStateType.UPDATE_PASSWORD_PROMPT -> {
                                //Must run on main thread
                                Fancy2FA.instance!!.server.scheduler.runTask(Fancy2FA.instance!!, Runnable {
                                        handleUpdatePasswordPrompt(player, lines!![0])
                                })
                                return true
                        }
                        PlayerStateType.VERIFICATION_PASSWORD_PROMPT -> {
                                //Must run on main thread
                                Fancy2FA.instance!!.server.scheduler.runTask(Fancy2FA.instance!!, Runnable {
                                        handleEnterPasswordPrompt(player, lines!![0])
                                })
                                return true
                        }
                        PlayerStateType.VERIFICATION_QUESTION_PROMPT -> {
                                //Must run on main thread
                                Fancy2FA.instance!!.server.scheduler.runTask(Fancy2FA.instance!!, Runnable {
                                        handleEnterQuestionAnswerPrompt(player, lines!![0])
                                })
                                return true
                        }
                        else -> return false
                }

        }





}


