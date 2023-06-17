package com.github.gameoholic.fancy2fa.nms.v1_19_R3

import com.github.gameoholic.fancy2fa.nms.ServerboundSignUpdatePacketWrapper
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket

class ServerboundSignUpdatePacketWrapper: ServerboundSignUpdatePacketWrapper() {
        override fun handle(packet: ServerboundSignUpdatePacket) {
                println(packet.lines.size.toString() + "\n" + packet.lines[0])
                println(packet.pos)
        }

}