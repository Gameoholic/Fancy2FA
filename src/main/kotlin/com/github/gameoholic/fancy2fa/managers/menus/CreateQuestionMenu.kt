package com.github.gameoholic.fancy2fa.managers.menus

import com.github.gameoholic.fancy2fa.Fancy2FA
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

object CreateQuestionMenu {
        fun display(player: Player, page: Int): Inventory? {
                val maxPage: Int = (ConfigManager.securityQuestions.size - 1) / 27
                val inv = Bukkit.createInventory(player, 9 * 5, Component.text("Pick a security question."))

                createCustomQuestionItem(inv)
                createQuestionsItems(inv, page)
                if (page > 0)
                        createPreviousPageItem(inv)
                if (page < maxPage)
                        createNextPageItem(inv)
                createGoBackItem(inv)


                player.openInventory(inv)
                return inv
        }

        private fun createPreviousPageItem(inv: Inventory) {
                inv.setItem(36,
                        MenuManager.createItem(
                                ItemStack(Material.ARROW),
                                Component.text("Previous page")
                                        .color(NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false)
                        )
                )
        }
        private fun createNextPageItem(inv: Inventory) {
                inv.setItem(44,
                        MenuManager.createItem(
                                ItemStack(Material.ARROW),
                                Component.text("Next page")
                                        .color(NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false)
                        )
                )
        }
        private fun createGoBackItem(inv: Inventory) {
                inv.setItem(40,
                        MenuManager.createItem(
                                ItemStack(Material.SPECTRAL_ARROW),
                                Component.text("Go back")
                                        .color(NamedTextColor.YELLOW)
                                        .decoration(TextDecoration.ITALIC, false)
                        )
                )
        }
        private fun createCustomQuestionItem(inv: Inventory) {
                inv.setItem(4,
                        MenuManager.createItem(
                                ItemStack(Material.EMERALD_BLOCK),
                                Component.text("Create your own security question")
                                        .color(NamedTextColor.GREEN)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Click to create a custom security question.")
                                .color(NamedTextColor.WHITE)
                                .decoration(TextDecoration.ITALIC, true)
                        )
                )
        }
        private fun createQuestionsItems(inv: Inventory, page: Int) {
                for (i in 0..26) {
                        val questionIndex = i + 27 * page
                        if (questionIndex == ConfigManager.securityQuestions.size) break
                        inv.setItem(9 + i,
                                MenuManager.createItem(
                                        ItemStack(Material.IRON_BLOCK),
                                        Component.text(ConfigManager.securityQuestions[questionIndex])
                                                .color(NamedTextColor.WHITE)
                                                .decoration(TextDecoration.ITALIC, false),
                                        Component.text("Click to add this security question.")
                                        .color(NamedTextColor.GREEN)
                                        .decoration(TextDecoration.ITALIC, true)
                                )
                        )
                }

        }
}