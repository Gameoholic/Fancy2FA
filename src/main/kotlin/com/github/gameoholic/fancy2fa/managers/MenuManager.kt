package com.github.gameoholic.fancy2fa.managers

import com.github.gameoholic.fancy2fa.Fancy2FA
import com.github.gameoholic.fancy2fa.datatypes.*
import com.github.gameoholic.fancy2fa.managers.menus.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object MenuManager {


        fun handleMenuItemClick(player: Player, item: ItemStack, playerState: PlayerStateType, slot: Int) {
                var itemName = PlainTextComponentSerializer.plainText().serialize(item.itemMeta.displayName()!!)

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
                                if (itemName == "Create your own security question")
                                        PromptManager.promptCreateCustomQuestion(player)
                                else if (itemName == "Next page") {
                                        val questionPage = Fancy2FA.instance?.playerState?.get(player.uniqueId)?.data!! as Int
                                        displayCreateQuestionMenu(player, questionPage + 1)
                                }
                                else if (itemName == "Previous page") {
                                        val questionPage = Fancy2FA.instance?.playerState?.get(player.uniqueId)?.data!! as Int
                                        displayCreateQuestionMenu(player, questionPage - 1)
                                }
                                else if (itemName == "Go back") {
                                        displayMainMenu(player)
                                }
                                else {
                                        val doesSecurityQuestionExist: Boolean = (DBManager.runDBOperation(DBManager.doesSecurityQuestionExist(itemName, player.uniqueId), player) ?: return).result
                                        if (doesSecurityQuestionExist) {
                                                displayErrorMenu(player, "You've already created this security question, please pick another one.")
                                                return
                                        }
                                        PromptManager.promptQuestionAnswer(player, itemName)
                                }
                        }

                        PlayerStateType.QUESTION_DETAILS_MENU -> {
                                val question = Fancy2FA.instance?.playerState?.get(player.uniqueId)?.data!!.toString()
                                if (itemName == "Remove question") {
                                        DBManager.removeSecurityQuestion(question, player.uniqueId)
                                        displayMainMenu(player)
                                }
                                else if (itemName == "Update answer") {
                                        PromptManager.promptQuestionUpdate(player, question)
                                }
                                else if (itemName == "Go back") {
                                        displayMainMenu(player)
                                }
                        }

                        PlayerStateType.ERROR_MENU ->  {
                                //If needs to return back to verification menu:
                                if (Fancy2FA.instance?.unverifiedPlayers!!.contains(player.uniqueId)) {
                                        val securityQuestions: MutableList<SecurityQuestion> =
                                                (DBManager.runDBOperation(DBManager.getPlayerSecurityQuestions(player.uniqueId)) ?: return).result
                                        val authData: PlayerPasswordData? =
                                                (DBManager.runDBOperation(DBManager.getPlayerPasswordData(player.uniqueId)) ?: return).result
                                        val discordAuthData: DiscordAuthData? =
                                                (DBManager.runDBOperation(DBManager.getPlayerDiscordAuthData(player.uniqueId)) ?: return).result
                                        val fullAuthData = Player2FALoginData(0, securityQuestions, authData, discordAuthData)
                                        displayVerificationMenu(player, fullAuthData)
                                }
                                else
                                        displayMainMenu(player)
                        }

                        PlayerStateType.PASSWORD_MENU -> {
                                if (itemName == "Go back")
                                        displayMainMenu(player)
                                else if (itemName == "Click to set a password.")
                                        PromptManager.promptCreatePassword(player)
                                else if (itemName == "Remove password") {
                                        DBManager.removePassword(player.uniqueId)
                                        displayMainMenu(player)
                                }
                                else if (itemName == "Update password") {
                                        PromptManager.promptUpdatePassword(player)
                                }
                        }

                        PlayerStateType.DISCORD_MENU -> {
                                if (itemName == "Go back")
                                        displayMainMenu(player)
                                else if (itemName == "Remove account") {
                                        DBManager.removeDiscordAuth(player.uniqueId)
                                        displayMainMenu(player)
                                }
                                else if (itemName == "Update account") {
                                        PromptManager.promptDiscordAuthProcess(player)
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
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.INFO_MENU, menuInventory = inv))
        }
        fun displayMainMenu(player: Player) {
                val inv = MainMenu.display(player)
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.MAIN_MENU, menuInventory = inv))
        }
        fun displayVerificationMenu(player: Player, fullAuthData: Player2FALoginData) {
                val inv = VerificationMenu.display(player, fullAuthData)

                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.AUTH_LOGIN_MENU, fullAuthData, inv))
        }
        fun displayErrorMenu(player: Player, errorMessage: String) {
                val inv = ErrorMenu.display(player, errorMessage)
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.ERROR_MENU, menuInventory = inv))
        }
        private fun displayDiscordMenu(player: Player) {
                //player state is updated in DiscordMenu as needed
                DiscordMenu.display(player)
        }
        private fun displayPasswordMenu(player: Player) {
                val inv = PasswordMenu.display(player)
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.PASSWORD_MENU, menuInventory = inv))
        }
        private fun displayCreateQuestionMenu(player: Player, page: Int = 0) {
                val inv = CreateQuestionMenu.display(player, page)
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.CREATE_QUESTION_MENU, page, inv)) //page
        }
        private fun displayQuestionDetailsMenu(player: Player, itemStack: ItemStack, itemName: Component, question: String) {
                val inv = QuestionDetailsMenu.display(player, itemStack, itemName, question)
                Fancy2FA.instance?.playerState?.put(player.uniqueId, PlayerState(PlayerStateType.QUESTION_DETAILS_MENU, question, inv))
        }

        fun createItem(item: ItemStack, name: Component, vararg lore: Component): ItemStack {
                var meta = item.itemMeta;
                meta.displayName(name)

                var lores = lore.toMutableList()
                meta.lore(lores)

                item.itemMeta = meta
                return item
        }
        fun createItem(item: ItemStack, name: Component, lore: MutableList<TextComponent>): ItemStack {
                var meta = item.itemMeta;
                meta.displayName(name)
                meta.lore(lore)
                item.itemMeta = meta
                return item
        }



}