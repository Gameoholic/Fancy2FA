package com.github.gameoholic.fancy2fa.managers.menus

import com.github.gameoholic.fancy2fa.datatypes.DiscordAuthData
import com.github.gameoholic.fancy2fa.datatypes.Player2FALoginData
import com.github.gameoholic.fancy2fa.datatypes.PlayerPasswordData
import com.github.gameoholic.fancy2fa.datatypes.SecurityQuestion
import com.github.gameoholic.fancy2fa.managers.ConfigManager
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

object VerificationMenu {

        fun display(player: Player, authData: Player2FALoginData): Inventory? {
                val inv = Bukkit.createInventory(player, 9 * 3, Component.text("Select an authentication method").color(NamedTextColor.RED))

                //todo: change auth dat ato password data everywehre

                if (authData.passwordData != null)
                        createPasswordItem(inv)
                if (authData.discordAuthData != null)
                        createDiscordItem(inv)
                if (authData.securityQuestions != null && authData.securityQuestions.size >= ConfigManager.questionsRequired) {
                        createSecurityQuestionItems(inv, authData.securityQuestions)
                }

                player.openInventory(inv)
                return inv
        }

        private fun createPasswordItem(inv: Inventory) {
                inv.setItem(4,
                        MenuManager.createItem(
                                ItemStack(Material.REDSTONE_TORCH),
                                Component.text("Authenticate via password.")
                                        .color(NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Click to proceed.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, true)
                        )
                )
        }

        private fun createDiscordItem(inv: Inventory) {
                inv.setItem(22,
                        MenuManager.createItem(
                                ItemStack(Material.BLUE_SHULKER_BOX),
                                Component.text("Authenticate via Discord.")
                                        .color(NamedTextColor.BLUE)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Click to proceed.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, true)
                        )
                )
        }


        private fun createSecurityQuestionItems(inv: Inventory, questions: MutableList<SecurityQuestion>) {
                val questionMinIndex = (13 - floor(questions.size / 2.0)).toInt()

                for (question in questions) {
                        val i = questions.indexOf(question)

                        inv.setItem(questionMinIndex + i,
                                MenuManager.createItem(
                                        ItemStack(Material.WRITABLE_BOOK),
                                        Component.text(question.question)
                                                .color(NamedTextColor.GREEN)
                                                .decoration(TextDecoration.ITALIC, false),
                                        Component.text("Click to answer the security question.")
                                                .color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.ITALIC, true)
                                )
                        )
                }
        }


}