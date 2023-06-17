package com.github.gameoholic.fancy2fa.managers.menus

import com.github.gameoholic.fancy2fa.managers.DBManager
import com.github.gameoholic.fancy2fa.managers.MenuManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object PasswordMenu {

        private val passwordGuidelines = mutableListOf<TextComponent>(
                Component.text("Password guidelines:")
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false),
                Component.text("Your password must be between 5-15 characters.")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
                Component.text("It is recommended you include letters, numbers and special characters (&,*,@..)")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
                Component.text("Avoid using common words or easily guessable information, such as your name, birthdate, or phone number.")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
                Component.text("If you suspect that your password has been compromised, change it immediately.")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false),
                Component.text("Do NOT use your Minecraft/Microsoft password.")
                .color(NamedTextColor.RED)
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false)
        )


        fun display(player: Player): Inventory? {
                val isPasswordSet: Boolean = (DBManager.runDBOperation(DBManager.hasPassword(player.uniqueId), player) ?: return null).result

                if (!isPasswordSet) {
                        val inv = Bukkit.createInventory(player, 9 * 2, Component.text("Password creation"))

                        addGoBackMenuItem(inv)
                        addPasswordItem(inv, isPasswordSet)

                        player.openInventory(inv)
                        return inv
                }
                else {
                        val inv = Bukkit.createInventory(player, 9 * 3, Component.text("Your password"))

                        addGoBackMenuItem(inv)
                        addPasswordItem(inv, isPasswordSet)
                        addRemovePasswordItem(inv)
                        addUpdatePasswordItem(inv)

                        player.openInventory(inv)
                        return inv
                }
        }

        private fun addUpdatePasswordItem(inv: Inventory) {
                val lore = mutableListOf<TextComponent>(
                        Component.text("Click to update your password.")
                                .color(NamedTextColor.WHITE)
                                .decoration(TextDecoration.ITALIC, true)
                )
                lore.addAll(passwordGuidelines)

                inv.setItem(23,
                        MenuManager.createItem(
                                ItemStack(Material.OAK_SIGN),
                                Component.text("Update password")
                                        .color(NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false),
                                lore
                        ))
        }

        private fun addRemovePasswordItem(inv: Inventory) {
                inv.setItem(21,
                        MenuManager.createItem(
                                ItemStack(Material.BARRIER),
                                Component.text("Remove password")
                                        .color(NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Click to remove the password.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, true)
                        ))
        }

        private fun addPasswordItem(inv: Inventory, passwordSet: Boolean) {
                if (passwordSet) {
                        inv.setItem(4,
                                MenuManager.createItem(
                                        ItemStack(Material.REDSTONE_TORCH),
                                        Component.text("Password already set.")
                                                .color(NamedTextColor.RED)
                                                .decoration(TextDecoration.ITALIC, false)
                                ))
                }
                else {
                        inv.setItem(4,
                                MenuManager.createItem(
                                        ItemStack(Material.REDSTONE_TORCH),
                                        Component.text("Click to set a password.")
                                                .color(NamedTextColor.RED)
                                                .decoration(TextDecoration.ITALIC, false),
                                        passwordGuidelines
                                ))
                }
        }

        private fun addGoBackMenuItem(inv: Inventory) {
                inv.setItem(13,
                        MenuManager.createItem(
                                ItemStack(Material.SPECTRAL_ARROW),
                                Component.text("Go back")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false)
                        )
                )
        }
}