package com.github.gameoholic.fancy2fa.managers

import com.github.gameoholic.fancy2fa.Fancy2FA
import com.github.gameoholic.fancy2fa.datatypes.*
import com.github.gameoholic.fancy2fa.managers.menus.*
import com.github.gameoholic.fancy2fa.utils.DBUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object MenuManager {


        fun handleMenuItemClick(player: Player, item: ItemStack, playerState: PlayerStateType, slot: Int) {
                val itemName = PlainTextComponentSerializer.plainText().serialize(item.itemMeta.displayName()!!)

                when (playerState) {
                        PlayerStateType.MAIN_MENU -> {
                                if (itemName == "Add Security Question" || itemName == "Required security question")
                                        displayCreateQuestionMenu(player)
                                else if (itemName.startsWith("Security Question "))
                                        displayQuestionDetailsMenu(player,
                                                item,
                                                item.itemMeta.displayName()!!,
                                                PlainTextComponentSerializer.plainText().serialize(item.itemMeta.lore()!![0])
                                        )
                                else if (itemName == "Password")
                                        displayPasswordMenu(player)
                                else if (itemName == "Discord")
                                        displayDiscordMenu(player)
                        }

                        PlayerStateType.CREATE_QUESTION_MENU -> {
                                when (itemName) {
                                        "Create your own security question" -> PromptManager.promptCreateCustomQuestion(player)
                                        "Next page" -> {
                                                val questionPage = Fancy2FA.playerStates[player.uniqueId]?.data!! as Int
                                                displayCreateQuestionMenu(player, questionPage + 1)
                                        }
                                        "Previous page" -> {
                                                val questionPage = Fancy2FA.playerStates[player.uniqueId]?.data!! as Int
                                                displayCreateQuestionMenu(player, questionPage - 1)
                                        }
                                        "Go back" -> {
                                                displayMainMenu(player)
                                        }
                                        else -> {
                                                val doesSecurityQuestionExist: Boolean = (DBUtil.runDBOperation(
                                                        DBUtil.doesSecurityQuestionExist(itemName, player.uniqueId), player) ?: return).result
                                                if (doesSecurityQuestionExist) {
                                                        displayErrorMenu(player, "You've already created this security question, please pick another one.")
                                                        return
                                                }
                                                PromptManager.promptQuestionAnswer(player, itemName)
                                        }
                                }
                        }

                        PlayerStateType.QUESTION_DETAILS_MENU -> {
                                val question = Fancy2FA.playerStates[player.uniqueId]?.data!!.toString()
                                when (itemName) {
                                        "Remove question" -> {
                                                DBUtil.removeSecurityQuestion(question, player.uniqueId)
                                                displayMainMenu(player)
                                        }
                                        "Update answer" -> {
                                                PromptManager.promptQuestionUpdate(player, question)
                                        }
                                        "Go back" -> {
                                                displayMainMenu(player)
                                        }
                                }
                        }

                        PlayerStateType.ERROR_MENU ->  {
                                //If needs to return back to verification menu:
                                if (Fancy2FA.unverifiedPlayers.contains(player.uniqueId)) {
                                        val securityQuestions: MutableList<SecurityQuestion> =
                                                (DBUtil.runDBOperation(DBUtil.getPlayerSecurityQuestions(player.uniqueId)) ?: return).result
                                        val authData: PlayerPasswordData? =
                                                (DBUtil.runDBOperation(DBUtil.getPlayerPasswordData(player.uniqueId)) ?: return).result
                                        val discordAuthData: DiscordAuthData? =
                                                (DBUtil.runDBOperation(DBUtil.getPlayerDiscordAuthData(player.uniqueId)) ?: return).result
                                        val fullAuthData = Player2FALoginData(0, securityQuestions, authData, discordAuthData)
                                        displayVerificationMenu(player, fullAuthData)
                                }
                                else
                                        displayMainMenu(player)
                        }

                        PlayerStateType.PASSWORD_MENU -> {
                                when (itemName) {
                                        "Go back" -> displayMainMenu(player)
                                        "Click to set a password." -> PromptManager.promptCreatePassword(player)
                                        "Remove password" -> {
                                                DBUtil.removePassword(player.uniqueId)
                                                displayMainMenu(player)
                                        }
                                        "Update password" -> {
                                                PromptManager.promptUpdatePassword(player)
                                        }
                                }
                        }

                        PlayerStateType.DISCORD_MENU -> {
                                when (itemName) {
                                        "Go back" -> displayMainMenu(player)
                                        "Remove account" -> {
                                                DBUtil.removeDiscordAuth(player.uniqueId)
                                                displayMainMenu(player)
                                        }
                                        "Update account" -> {
                                                PromptManager.promptDiscordAuthProcess(player)
                                        }
                                }
                        }

                        PlayerStateType.INFO_MENU -> {
                                displayMainMenu(player)
                        }

                        PlayerStateType.AUTH_LOGIN_MENU -> {
                                if (itemName == "Authenticate via password.") {
                                        PromptManager.promptEnterPassword(player)
                                }
                                else if (itemName == "Authenticate via Discord.") {
                                        PromptManager.promptDiscordAuthProcess(player, true)
                                }
                                else if (item.type == Material.WRITABLE_BOOK && slot < 17)
                                        PromptManager.promptEnterQuestionAnswer(player, itemName)
                        }

                        else -> {}
                }

        }

        fun displayInfoMenu(player: Player) {
                val inv = InfoMenu.display(player)
                Fancy2FA.playerStates[player.uniqueId] = PlayerState(PlayerStateType.INFO_MENU, menuInventory = inv)
        }
        fun displayMainMenu(player: Player) {
                val inv = MainMenu.display(player)
                Fancy2FA.playerStates[player.uniqueId] = PlayerState(PlayerStateType.MAIN_MENU, menuInventory = inv)
        }
        fun displayVerificationMenu(player: Player, fullAuthData: Player2FALoginData) {
                val inv = VerificationMenu.display(player, fullAuthData)

                Fancy2FA.playerStates[player.uniqueId] = PlayerState(PlayerStateType.AUTH_LOGIN_MENU, fullAuthData, inv)
        }
        fun displayErrorMenu(player: Player, errorMessage: String) {
                val inv = ErrorMenu.display(player, errorMessage)
                Fancy2FA.playerStates[player.uniqueId] = PlayerState(PlayerStateType.ERROR_MENU, menuInventory = inv)
        }
        private fun displayDiscordMenu(player: Player) {
                //player state is updated in DiscordMenu as needed
                DiscordMenu.display(player)
        }
        private fun displayPasswordMenu(player: Player) {
                val inv = PasswordMenu.display(player)
                Fancy2FA.playerStates[player.uniqueId] = PlayerState(PlayerStateType.PASSWORD_MENU, menuInventory = inv)
        }
        private fun displayCreateQuestionMenu(player: Player, page: Int = 0) {
                val inv = CreateQuestionMenu.display(player, page)
                Fancy2FA.playerStates[player.uniqueId] =
                        PlayerState(PlayerStateType.CREATE_QUESTION_MENU, page, inv) //page
        }
        private fun displayQuestionDetailsMenu(player: Player, itemStack: ItemStack, itemName: Component, question: String) {
                val inv = QuestionDetailsMenu.display(player, itemStack, itemName, question)
                Fancy2FA.playerStates[player.uniqueId] =
                        PlayerState(PlayerStateType.QUESTION_DETAILS_MENU, question, inv)
        }

        fun createItem(item: ItemStack, name: Component, vararg lore: Component): ItemStack {
                val meta = item.itemMeta
                meta.displayName(name)

                val lores = lore.toMutableList()
                meta.lore(lores)

                item.itemMeta = meta
                return item
        }
        fun createItem(item: ItemStack, name: Component, lore: MutableList<TextComponent>): ItemStack {
                val meta = item.itemMeta
                meta.displayName(name)
                meta.lore(lore)
                item.itemMeta = meta
                return item
        }



}