package com.lingyun.lib.im.socket.handler

import com.lingyun.lib.im.PacketFactory
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import proto.message.AckStatus
import proto.message.Packet
import proto.message.Ping
import timber.log.Timber

/*
* Created by mc_luo on 2021/5/3 .
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
class TimeOutHandler : ChannelDuplexHandler() {
    val ping = Ping.newBuilder().build()

    @Throws(Exception::class)
    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        if (evt is IdleStateEvent) {
            when (evt.state()) {
                IdleState.READER_IDLE -> {
                    Timber.e("read time out to disconnect channel")
                    ctx.close()
                }
                IdleState.WRITER_IDLE -> {
                    ctx.writeAndFlush(newPingPacket())
                }
                IdleState.ALL_IDLE -> {
                    Timber.e("all time out")
                    ctx.close()
                }
                else -> {

                }
            }
        }
    }

    fun newPingPacket(): Packet {
        return PacketFactory.newPacketBuild()
            .also {
                it.ackStatus = AckStatus.NO_ACK
                it.ping = ping
            }
            .build()
    }
}
