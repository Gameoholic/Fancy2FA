package com.github.gameoholic.fancy2fa.nms.v1_19_R2

import com.github.gameoholic.fancy2fa.nms.SendFakeSign
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket
import net.minecraft.world.level.block.entity.SignBlockEntity
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_19_R3.block.data.CraftBlockData
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer
import org.bukkit.entity.Player

class SendFakeSign : SendFakeSign() {
        override fun sendFakeSignPacket(player: Player, vararg lines: String) {
                var craftPlayer: CraftPlayer = player as CraftPlayer
                val serverPlayer = craftPlayer.handle
                val listener = serverPlayer.connection

                val blockPos = BlockPos(player.location.blockX, -64, player.location.blockZ)
                val blockState = (Material.OAK_SIGN.createBlockData() as CraftBlockData).state
                val craftSign = SignBlockEntity(blockPos, blockState)
                val messages = lines.toMutableList()
                for (i in messages.indices) {
                        craftSign.setMessage(i, net.minecraft.network.chat.Component.literal(messages[i]))
                }

                var blockUpdatePacket = ClientboundBlockUpdatePacket(blockPos, craftSign.blockState)
                val blockEntityDataPacket = craftSign.updatePacket
                val openSignEditorPacket = ClientboundOpenSignEditorPacket(blockPos)

                listener.send(blockUpdatePacket)
                listener.send(blockEntityDataPacket)
                listener.send(openSignEditorPacket)
        }

}