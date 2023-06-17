package com.github.gameoholic.fancy2fa.managers.menus

import com.github.gameoholic.fancy2fa.datatypes.DiscordAuthData
import com.github.gameoholic.fancy2fa.datatypes.SecurityQuestion
import com.github.gameoholic.fancy2fa.managers.ConfigManager
import com.github.gameoholic.fancy2fa.managers.DBManager
import com.github.gameoholic.fancy2fa.managers.MenuManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.math.floor
import kotlin.math.max


object MainMenu {
        private val questionMaterials = arrayOf(
                Material.RED_WOOL,
                Material.ORANGE_WOOL,
                Material.YELLOW_WOOL,
                Material.LIME_WOOL,
                Material.GREEN_WOOL,
                Material.CYAN_WOOL,
                Material.LIGHT_BLUE_WOOL,
                Material.BLUE_WOOL,
                Material.PURPLE_WOOL
        )
        private val questionColors = arrayOf(
                NamedTextColor.RED,
                NamedTextColor.GOLD,
                NamedTextColor.YELLOW,
                NamedTextColor.GREEN,
                NamedTextColor.DARK_GREEN,
                NamedTextColor.DARK_AQUA,
                NamedTextColor.AQUA,
                NamedTextColor.BLUE,
                NamedTextColor.DARK_PURPLE
        )


        fun display(player: Player): Inventory? {
                val questionsRequired = ConfigManager.questionsRequired


                val hasPassword: Boolean = (DBManager.runDBOperation(DBManager.hasPassword(player.uniqueId), player) ?: return null).result
                val questions: MutableList<SecurityQuestion> = (DBManager.runDBOperation(
                        DBManager.getPlayerSecurityQuestions(player.uniqueId), player) ?: return null).result

                val discordAuthData: DiscordAuthData? = (DBManager.runDBOperation(
                        DBManager.getPlayerDiscordAuthData(player.uniqueId), player) ?: return null).result

                val questionMinIndex = (13 - floor(max(questions.size, questionsRequired) / 2.0)).toInt()

                val inv = Bukkit.createInventory(player, 9 * 3, Component.text("Please create credentials."))

                addAddSecurityQuestionItem(inv, questions, questionMinIndex, questionsRequired)
                if (questions.size > 0) { //Only display if player picked security questions as 2FA method
                        addSecurityQuestionsItems(inv, questions, questionMinIndex)
                        addRequiredSecurityQuestionsItems(inv, questions, questionMinIndex, questionsRequired)
                }

                addDiscordItem(inv, discordAuthData)
                addPasswordItem(inv, hasPassword)

                player.openInventory(inv)

                return inv
        }

        private fun addAddSecurityQuestionItem(
                inv: Inventory, questions: MutableList<SecurityQuestion>, questionMinIndex: Int, questionsRequired: Int
        ) {
                var index = questionMinIndex + questions.size
                if (questions.size == 0)
                        index = 13 //In case security questions were not picked as 2FA method
                if (questions.size < 9 && questionsRequired < 9) {
                        inv.setItem(
                                index,
                                MenuManager.createItem(
                                        ItemStack(Material.OAK_BUTTON),
                                        Component.text("Add Security Question")
                                                .color(NamedTextColor.GREEN)
                                                .decoration(TextDecoration.ITALIC, false),
                                        Component.text("Click to add an additional security question.")
                                                .color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.ITALIC, false)
                                )
                        )
                }
        }


        private fun addRequiredSecurityQuestionsItems(
                inv: Inventory, questions: MutableList<SecurityQuestion>, questionMinIndex: Int, questionsRequired: Int
        ) {
                for (i in 0 until questionsRequired - questions.size) {
                        inv.setItem(
                                questionMinIndex + questions.size + i,
                                MenuManager.createItem(
                                        ItemStack(Material.BEDROCK),
                                        Component.text("Required security question")
                                                .color(NamedTextColor.DARK_RED)
                                                .decoration(TextDecoration.ITALIC, true),
                                        Component.text("You're required to add an additional security question.")
                                                .color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.ITALIC, false),
                                        Component.text("Click to add one.")
                                                .color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.ITALIC, true)
                                )
                        )
                }
        }

        private fun addSecurityQuestionsItems(inv: Inventory, questions: MutableList<SecurityQuestion>, questionMinIndex: Int) {
                for (question in questions) {
                        val i = questions.indexOf(question)

                        val lore = mutableListOf(
                                Component.text(question.question)
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false)
                        )

                        inv.setItem(
                                questionMinIndex + i,
                                MenuManager.createItem(
                                        ItemStack(questionMaterials[i], i + 1),
                                        Component.text("Security Question ${i + 1}")
                                                .color(questionColors[i])
                                                .decoration(TextDecoration.ITALIC, false),
                                        lore
                                )
                        )
                }
        }

        private fun addDiscordItem(inv: Inventory, discordAuthData: DiscordAuthData?) {  //discordAuthData Will be null if user not discord authed
                if (discordAuthData == null)
                        inv.setItem(
                                22,
                                MenuManager.createItem(
                                        ItemStack(Material.BLUE_SHULKER_BOX),
                                        Component.text("Discord")
                                                .color(NamedTextColor.BLUE)
                                                .decoration(TextDecoration.ITALIC, false),
                                        Component.text("Click to set up Discord authentication.")
                                                .color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.ITALIC, false)
                                )
                        )
                else
                        inv.setItem(
                                22,
                                MenuManager.createItem(
                                        ItemStack(Material.BLUE_SHULKER_BOX),
                                        Component.text("Discord")
                                                .color(NamedTextColor.BLUE)
                                                .decoration(TextDecoration.ITALIC, false),
                                        Component.text("Authenticated as " + discordAuthData.username)
                                                .color(NamedTextColor.GREEN)
                                                .decoration(TextDecoration.ITALIC, false),
                                        Component.text("Click to view your Discord authentication status.")
                                                .color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.ITALIC, false)
                                )
                        )
        }

        private fun addPasswordItem(inv: Inventory, hasPassword: Boolean) {
                if (!hasPassword)
                        inv.setItem(
                                4,
                                MenuManager.createItem(
                                        ItemStack(Material.REDSTONE_TORCH),
                                        Component.text("Password")
                                                .color(NamedTextColor.RED)
                                                .decoration(TextDecoration.ITALIC, false),
                                        Component.text("Click to set a password for your account.")
                                                .color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.ITALIC, false)
                                ))
                else
                        inv.setItem(
                                4,
                                MenuManager.createItem(
                                        ItemStack(Material.REDSTONE_TORCH),
                                        Component.text("Password")
                                                .color(NamedTextColor.RED)
                                                .decoration(TextDecoration.ITALIC, false),
                                        Component.text("Click to edit your password.")
                                                .color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.ITALIC, false),
                                )
                        )
        }

}