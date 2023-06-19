package com.github.gameoholic.fancy2fa.managers.menus

import com.github.gameoholic.fancy2fa.Fancy2FA
import com.github.gameoholic.fancy2fa.datatypes.DiscordAuthData
import com.github.gameoholic.fancy2fa.datatypes.PlayerState
import com.github.gameoholic.fancy2fa.datatypes.PlayerStateType
import com.github.gameoholic.fancy2fa.utils.DBUtil
import com.github.gameoholic.fancy2fa.managers.MenuManager
import com.github.gameoholic.fancy2fa.managers.PromptManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object DiscordMenu {


        fun display(player: Player) {
                val isDiscordAuthed: Boolean = (DBUtil.runDBOperation(DBUtil.isDiscordAuthed(player.uniqueId), player) ?: return).result

                if (isDiscordAuthed) {
                        val discordAuthData: DiscordAuthData = (DBUtil.runDBOperation(
                                DBUtil.getPlayerDiscordAuthData(player.uniqueId), player) ?: return).result ?: return
                        val inv = Bukkit.createInventory(player, 9 * 3, Component.text("Discord authentication"))
                        addGoBackItem(inv)
                        addRemoveDiscordItem(inv)
                        addUpdateDiscordItem(inv)
                        addDiscordAuthItem(inv, discordAuthData.username)
                        player.openInventory(inv)
                        Fancy2FA.playerStates[player.uniqueId] = PlayerState(PlayerStateType.DISCORD_MENU, null, inv)
                }
                else {
                        PromptManager.promptDiscordAuthProcess(player)
                }
        }

        private fun addUpdateDiscordItem(inv: Inventory) {
                inv.setItem(5,
                        MenuManager.createItem(
                                ItemStack(Material.OAK_SIGN),
                                Component.text("Update account")
                                        .color(NamedTextColor.BLUE)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Click to change the Discord account used for authentication.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, true)
                        )
                )
        }
        private fun addRemoveDiscordItem(inv: Inventory) {
                inv.setItem(3,
                        MenuManager.createItem(
                                ItemStack(Material.BARRIER),
                                Component.text("Remove account")
                                        .color(NamedTextColor.BLUE)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Click to remove the account used for authentication.")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, true)
                        )
                )
        }
        private fun addGoBackItem(inv: Inventory) {
                inv.setItem(13,
                        MenuManager.createItem(
                                ItemStack(Material.SPECTRAL_ARROW),
                                Component.text("Go back")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false)
                        )
                )
        }
        private fun addDiscordAuthItem(inv: Inventory, discordUsername: String) {
                inv.setItem(22,
                        MenuManager.createItem(
                                ItemStack(Material.BLUE_SHULKER_BOX),
                                Component.text("You're authenticated via Discord.")
                                        .color(NamedTextColor.BLUE)
                                        .decoration(TextDecoration.ITALIC, false),
                                Component.text("Username: ")
                                        .color(NamedTextColor.WHITE)
                                        .decoration(TextDecoration.ITALIC, false)
                                        .append(
                                                Component.text(discordUsername)
                                                .color(NamedTextColor.GREEN)
                                                .decoration(TextDecoration.ITALIC, false)
                                        )
                        )
                )
        }
}