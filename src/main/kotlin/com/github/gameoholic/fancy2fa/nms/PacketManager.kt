package com.github.gameoholic.fancy2fa.nms

import org.bukkit.entity.Player

abstract class PacketManager {
        abstract fun createSendFakeSign(): SendFakeSign
        abstract fun createServerboundSignUpdatePacketWrapper(): ServerboundSignUpdatePacketWrapper
        abstract fun injectPlayer(player: Player)
        abstract fun removePlayer(player: Player)
}