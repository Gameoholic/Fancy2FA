package com.github.gameoholic.fancy2fa.tasks

import com.github.gameoholic.fancy2fa.Fancy2FA
import org.bukkit.Location
import java.util.*

class TeleportUnauthedPlayersTask: Runnable {

        override fun run() {
                for (player: MutableMap.MutableEntry<UUID, Location> in Fancy2FA.instance?.unverifiedPlayers!!) {
                        val player = Fancy2FA.instance?.server?.getPlayer(player.key) ?: return

                        //player.teleport(player.value)
                }
        }

}