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

object ErrorMenu {
        fun display(player: Player, errorMessage: String): Inventory? {
                val inv = Bukkit.createInventory(player, 9 * 2, Component.text("Error").color(NamedTextColor.RED))

                createErrorItem(inv, errorMessage)
                createGoBackItem(inv)

                player.openInventory(inv)
                return inv
        }

        private fun createErrorItem(inv: Inventory, errorMessage: String) {
                inv.setItem(4,
                        MenuManager.createItem(
                                ItemStack(Material.BARRIER),
                                Component.text(errorMessage)
                                        .color(NamedTextColor.RED)
                                        .decoration(TextDecoration.ITALIC, false)
                        )
                )
        }
        private fun createGoBackItem(inv: Inventory) {
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