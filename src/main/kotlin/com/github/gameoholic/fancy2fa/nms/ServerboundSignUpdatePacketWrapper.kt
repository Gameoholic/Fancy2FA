package com.github.gameoholic.fancy2fa.nms

import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket

abstract class ServerboundSignUpdatePacketWrapper {

        abstract fun handle(packet: ServerboundSignUpdatePacket)

}