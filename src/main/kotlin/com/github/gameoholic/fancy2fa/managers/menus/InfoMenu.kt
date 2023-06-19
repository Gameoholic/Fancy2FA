package com.github.gameoholic.fancy2fa.managers.menus

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

object InfoMenu {

        fun display(player: Player): Inventory? {
                val inv = Bukkit.createInventory(player, 9 * 1, Component.text("2FA Set Up"))

                addInfoItem(inv, ConfigManager.questionsRequired, ConfigManager.answerQuestionsRequired, ConfigManager.authenticationIPCooldown)

                player.openInventory(inv)
                return inv
        }

        private fun addInfoItem(inv: Inventory, questionsRequired: Int, answerQuestionsRequired: Int, authCooldown: Int) {
                inv.setItem(
                        4,
                        MenuManager.createItem(
                                ItemStack(Material.PAPER),
                                Component.text("2FA instructions")
                                        .color(NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("You are required to use at least ")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false)
                                        .append(
                                                Component.text("1")
                                                        .color(NamedTextColor.GREEN)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        )
                                        .append(
                                                Component.text(" authentication method/s as per your position ")
                                                        .color(NamedTextColor.WHITE)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        ),
                                Component.text("on the server.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Available authentication methods are: ")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false)
                                        .append(
                                                Component.text("Discord")
                                                        .color(NamedTextColor.BLUE)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        )
                                        .append(
                                                Component.text(", ")
                                                        .color(NamedTextColor.WHITE)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        )
                                        .append(
                                                Component.text("Security Questions")
                                                        .color(NamedTextColor.YELLOW)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        )
                                        .append(
                                                Component.text(", ")
                                                        .color(NamedTextColor.WHITE)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        )
                                        .append(
                                                Component.text("Password")
                                                        .color(NamedTextColor.RED)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        )
                                        .append(
                                                Component.text(".")
                                                        .color(NamedTextColor.WHITE)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        ),
                                Component.text("If you choose to use security questions, you must create at least")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text(questionsRequired)
                                        .color(NamedTextColor.GREEN)
                                        .decoration(TextDecoration.ITALIC, false)
                                        .append(
                                                Component.text(" security question/s, and you will have to answer ")
                                                        .color(NamedTextColor.WHITE)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        )
                                        .append(
                                                Component.text(answerQuestionsRequired)
                                                        .color(NamedTextColor.GREEN)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        )
                                        .append(
                                                Component.text(" question/s upon authentication.")
                                                        .color(NamedTextColor.WHITE)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        ),
                                Component.text("You will be required to re-authenticate once every ")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false)
                                        .append(
                                                Component.text(authCooldown)
                                                        .color(NamedTextColor.GREEN)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        )
                                        .append(
                                                Component.text(" days,")
                                                        .color(NamedTextColor.WHITE)
                                                        .decoration(TextDecoration.ITALIC, false)
                                        ),
                                Component.text("or when your IP changes.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Click to proceed.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, true)
                        )
                )
        }

}