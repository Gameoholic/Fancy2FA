package com.github.gameoholic.fancy2fa

import com.github.gameoholic.fancy2fa.commands.MainCommand
import com.github.gameoholic.fancy2fa.datatypes.PlayerState
import com.github.gameoholic.fancy2fa.listeners.*
import com.github.gameoholic.fancy2fa.managers.ConfigManager
import com.github.gameoholic.fancy2fa.nms.PacketManager

import org.apache.commons.io.IOUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Level


object Fancy2FA {
        lateinit var plugin: Fancy2FAPlugin
                private set

        lateinit var packetManager: PacketManager
                private set
        val playerStates = mutableMapOf<UUID, PlayerState>()
        val discordAuthStates = mutableMapOf<String, UUID>()
        val unverifiedPlayers = mutableMapOf<UUID, Location>()

        fun onEnable(fancy2FAPlugin: Fancy2FAPlugin) {
                plugin = fancy2FAPlugin

                plugin.saveDefaultConfig();

                packetManager = createPacketManager()
                createSecurityQuestionsFile()
                ConfigManager.loadConfig();

                Bukkit.getPluginManager().registerEvents(LeaveListener, plugin)
                Bukkit.getPluginManager().registerEvents(LoginListener, plugin)
                Bukkit.getPluginManager().registerEvents(ChatMessageListener, plugin)
                Bukkit.getPluginManager().registerEvents(InventoryClickListener, plugin)
                Bukkit.getPluginManager().registerEvents(InventoryCloseListener, plugin)
                Bukkit.getPluginManager().registerEvents(BlockBreakListener, plugin)
                Bukkit.getPluginManager().registerEvents(PlayerMoveListener, plugin)
                Bukkit.getPluginManager().registerEvents(CommandPreprocessListener, plugin)
                Bukkit.getPluginManager().registerEvents(InteractListener, plugin)
                Bukkit.getPluginManager().registerEvents(DropItemListener, plugin)
                Bukkit.getPluginManager().registerEvents(CommandAutocompleteListener, plugin)
                Bukkit.getPluginManager().registerEvents(PlayerMoveListener, plugin)


                plugin.getCommand("2fa")?.setExecutor(MainCommand)




                com.github.gameoholic.fancy2fa.ktor.main()

        }



        private fun createSecurityQuestionsFile() {
                val questionsTemplateFile = plugin.getResource("security_questions.txt")
                val file = File(plugin.dataFolder, "security_questions.txt")
                if (!file.exists())
                        try {
                                file.createNewFile()
                                file.writeText(IOUtils.toString(questionsTemplateFile, StandardCharsets.UTF_8))
                        }
                        catch (e: Exception) {
                                plugin.logger.log(Level.SEVERE, "Couldn't create security questions file! " + e.message)
                                e.printStackTrace()
                        }
        }


        private fun createPacketManager(): PacketManager = when (val serverVersion = Bukkit.getServer().minecraftVersion) {
                "1.19.4" -> com.github.gameoholic.fancy2fa.nms.v1_19_R3.PacketManager()
                else -> throw UnsupportedOperationException("Unsupported Minecraft version: $serverVersion")
        }





}