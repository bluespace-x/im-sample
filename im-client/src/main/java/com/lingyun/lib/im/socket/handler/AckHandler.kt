package com.lingyun.lib.im.socket.handler


import com.lingyun.lib.im.PacketFactory
import com.lingyun.lib.im.socket.Message
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import kotlinx.coroutines.*
import proto.message.Ack
import proto.message.AckStatus
import proto.message.Packet
import java.util.concurrent.CancellationException

/*
* Created by mc_luo on 2021/3/20 .
* Copyright (c) 2021 The LingYun Authors. All rights reserved.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
class AckHandler() : ChannelDuplexHandler() {

    private lateinit var coroutineScope: CoroutineScope

    private val waitAckPackets = HashMap<Long, Message>()
    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
//        coroutineScope = CoroutineScope(ctx.executor().asCoroutineDispatcher() + SupervisorJob())
        coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        coroutineScope.cancel()

        for (u in waitAckPackets.entries) {
            u.value.close(CancellationException())
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        require(msg is Packet)

        if (msg.hasAck()) {
            waitAckPackets.remove(msg.ack.ackPacketId)?.ackMessage(msg)
        }

        if (msg.ackStatus == AckStatus.WAIT_ACK) {
            val ackPacket = PacketFactory.newPacketBuild()
                .also {
                    it.ackStatus = AckStatus.ACK
                    it.ack = Ack.newBuilder()
                        .also {
                            it.ackPacketId = msg.packetId
                        }
                        .build()
                }
                .build()
            val ackmsg = Message.newMessage(ackPacket)
            ctx.writeAndFlush(ackmsg)
        }

        super.channelRead(ctx, msg)
    }

    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise?) {
        require(msg is Message)
        if (msg.isNeedAck()) {
            waitAckPackets[msg.packet.packetId] = msg
            coroutineScope.launch {
                var ackSuccess = false

                repeat(msg.ackRetryCount) {
                    ctx.writeAndFlush(msg, promise)
                    val response = withTimeoutOrNull(3000) {
                        try {
                            msg.ackFuture?.await()
                        } catch (e: Exception) {
                            ackSuccess = false
                            return@withTimeoutOrNull null
                        }
                    }

                    if (response != null) {
                        ackSuccess = true
                        return@launch
                    }
                }

                if (!ackSuccess) {
                    msg.close()
                }
            }
        } else {
            super.write(ctx, msg, promise)
        }
    }
}