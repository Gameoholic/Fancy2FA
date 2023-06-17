package com.github.gameoholic.fancy2fa.nms.v1_19_R3

import com.github.gameoholic.fancy2fa.managers.PromptManager
import com.github.gameoholic.fancy2fa.nms.PacketManager
import com.github.gameoholic.fancy2fa.nms.SendFakeSign
import com.github.gameoholic.fancy2fa.nms.ServerboundSignUpdatePacketWrapper
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player

class PacketManager: PacketManager() {

        override fun createSendFakeSign(): SendFakeSign {
                return com.github.gameoholic.fancy2fa.nms.v1_19_R3.SendFakeSign()
        }

        override fun createServerboundSignUpdatePacketWrapper(): ServerboundSignUpdatePacketWrapper {
                return com.github.gameoholic.fancy2fa.nms.v1_19_R3.ServerboundSignUpdatePacketWrapper()
        }

        override fun injectPlayer(player: Player) {
                val channelDuplexHandler: ChannelDuplexHandler = object : ChannelDuplexHandler() {
                        //Serverbound:
                        @Throws(Exception::class)
                        override fun channelRead(channelHandlerContext: ChannelHandlerContext, packet: Any) {
                                if (packet is ServerboundSignUpdatePacket && PromptManager.onPlayerSignUpdate(player, packet.lines))
                                        return
                                super.channelRead(channelHandlerContext, packet)
                        }

                        //Clientbound:
                        @Throws(Exception::class)
                        override fun write(
                                channelHandlerContext: ChannelHandlerContext,
                                packet: Any,
                                channelPromise: ChannelPromise
                        )
                        {
                                super.write(channelHandlerContext, packet, channelPromise)
                        }
                }
                val pipeline = (player as CraftPlayer).handle.connection.connection.channel.pipeline()
                pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler)
        }

        override fun removePlayer(player: Player) {
                val channel = (player as CraftPlayer).handle.connection.connection.channel
                channel.eventLoop().submit<Any?> {
                        channel.pipeline().remove(player.getName())
                        null
                }
        }


}