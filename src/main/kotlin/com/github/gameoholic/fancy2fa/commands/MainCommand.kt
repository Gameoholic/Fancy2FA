package com.github.gameoholic.fancy2fa.commands

import com.github.gameoholic.fancy2fa.managers.MenuManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object MainCommand : CommandExecutor {
        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
                if (sender is Player)
                        MenuManager.displayInfoMenu(sender)
                return true
        }
}