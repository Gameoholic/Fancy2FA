package com.github.gameoholic.fancy2fa.managers.menus

import com.github.gameoholic.fancy2fa.managers.MenuManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object QuestionDetailsMenu {

        fun display(player: Player, woolItem: ItemStack, woolName: Component, question: String): Inventory? {

                val inv = Bukkit.createInventory(player, 9 * 3, woolName)

                createGoBackItem(inv)
                createQuestionItem(inv, woolItem, woolName, question)
                createUpdateQuestionItem(inv)
                createRemoveQuestionItem(inv)

                player.openInventory(inv)
                return inv
        }

        private fun createGoBackItem(inv: Inventory) {
                inv.setItem(4,
                        MenuManager.createItem(
                                ItemStack(Material.SPECTRAL_ARROW),
                                Component.text("Go back")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false)
                        )
                )
        }
        private fun createQuestionItem(inv: Inventory, woolItem: ItemStack, woolName: Component, question: String) {
                inv.setItem(13,
                        MenuManager.createItem(
                                woolItem,
                                Component.text(question)
                                        .color(woolName.color())
                                        .decoration(TextDecoration.ITALIC, false)

                        )
                )
        }
        private fun createRemoveQuestionItem(inv: Inventory) {
                inv.setItem(21,
                        MenuManager.createItem(
                                ItemStack(Material.BARRIER),
                                Component.text("Remove question")
                                        .color(NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Click to remove this security question.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, true)
                        )
                )
        }
        private fun createUpdateQuestionItem(inv: Inventory) {
                inv.setItem(23,
                        MenuManager.createItem(
                                ItemStack(Material.OAK_SIGN),
                                Component.text("Update answer")
                                        .color(NamedTextColor.GREEN)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Click to update this security question's answer.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, true)
                        )
                )
        }



}