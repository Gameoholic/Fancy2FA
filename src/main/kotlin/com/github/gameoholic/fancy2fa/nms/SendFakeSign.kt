package com.github.gameoholic.fancy2fa.nms

import org.bukkit.entity.Player

abstract class SendFakeSign {
        abstract fun sendFakeSignPacket(player: Player, vararg lines: String)
}