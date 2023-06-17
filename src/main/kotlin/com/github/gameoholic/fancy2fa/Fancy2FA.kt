package com.github.gameoholic.fancy2fa

import com.github.gameoholic.fancy2fa.commands.MainCommand
import com.github.gameoholic.fancy2fa.datatypes.PlayerState
import com.github.gameoholic.fancy2fa.listeners.*
import com.github.gameoholic.fancy2fa.managers.ConfigManager
import com.github.gameoholic.fancy2fa.nms.PacketManager
import com.github.gameoholic.fancy2fa.tasks.TeleportUnauthedPlayersTask

import org.apache.commons.io.IOUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Level


class Fancy2FA : JavaPlugin() {
        companion object {
                var instance: Fancy2FA? = null
                        private set
        }

        var packetManager: PacketManager? = null
                private set
        val playerState = mutableMapOf<UUID, PlayerState>()
        val discordAuthStates = mutableMapOf<String, UUID>()
        val unverifiedPlayers = mutableMapOf<UUID, Location>()

        override fun onEnable() {
                instance = this

                saveDefaultConfig();

                packetManager = createPacketManager()
                createSecurityQuestionsFile()
                ConfigManager.loadConfig();

                Bukkit.getPluginManager().registerEvents(LeaveListener, this)
                Bukkit.getPluginManager().registerEvents(LoginListener, this)
                Bukkit.getPluginManager().registerEvents(ChatMessageListener, this)
                Bukkit.getPluginManager().registerEvents(InventoryClickListener, this)
                Bukkit.getPluginManager().registerEvents(InventoryCloseListener, this)
                Bukkit.getPluginManager().registerEvents(BlockBreakListener, this)
                Bukkit.getPluginManager().registerEvents(PlayerMoveListener, this)
                Bukkit.getPluginManager().registerEvents(CommandPreprocessListener, this)
                Bukkit.getPluginManager().registerEvents(InteractListener, this)
                Bukkit.getPluginManager().registerEvents(DropItemListener, this)
                Bukkit.getPluginManager().registerEvents(CommandAutocompleteListener, this)
                Bukkit.getPluginManager().registerEvents(PlayerMoveListener, this)




                server.scheduler.runTaskTimer(this, TeleportUnauthedPlayersTask(), 2L, 2L)
                getCommand("2fa")?.setExecutor(MainCommand)




                com.github.gameoholic.fancy2fa.ktor.main()

        }



        private fun createSecurityQuestionsFile() {
                val questionsTemplateFile = getResource("security_questions.txt")
                val file = File(dataFolder, "security_questions.txt")
                if (!file.exists())
                        try {
                                file.createNewFile()
                                file.writeText(IOUtils.toString(questionsTemplateFile, StandardCharsets.UTF_8))
                        }
                        catch (e: Exception) {
                                logger.log(Level.SEVERE, "Couldn't create security questions file! " + e.message)
                                e.printStackTrace()
                        }
        }

        override fun onDisable() {

        }


        private fun createPacketManager(): PacketManager {
                return when (val serverVersion = Bukkit.getServer().minecraftVersion) {
                        "1.19.4" -> com.github.gameoholic.fancy2fa.nms.v1_19_R3.PacketManager()
                        else -> throw UnsupportedOperationException("Unsupported Minecraft version: $serverVersion")
                }
        }





}