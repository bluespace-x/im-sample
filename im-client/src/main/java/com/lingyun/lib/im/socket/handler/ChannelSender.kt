package com.lingyun.lib.im.socket.handler


import com.lingyun.lib.im.exception.IMException
import com.lingyun.lib.im.socket.Message
import io.netty.channel.ChannelHandlerContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import proto.message.CommandResponse
import proto.message.OperationResponse
import proto.message.Packet
import proto.message.Response

/*
* Created by mc_luo on 2021/3/21 .
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
class ChannelSender(
    val ctx: ChannelHandlerContext,
) {

    fun writePacketAsync(packet: Packet): Deferred<Boolean>? {
        val msg = Message.newMessage(packet)
        ctx.writeAndFlush(msg)
        return msg.ackFuture
    }

    @Throws(IMException::class)
    suspend fun writePacketWaitResponse(packet: Packet): Flow<Packet> {
        val chan = Channel<Packet>(Channel.UNLIMITED)
        val message = Message.newMessage(packet, chan)

        val f = message.consumerResponse()
        ctx.writeAndFlush(message)
        message.ackFuture?.await()
        return f
    }

    suspend fun sendCommand(packet: Packet): CommandResponse {
        return writePacketWaitResponse(packet).first { it.hasCommandResponse() && it.commandResponse.commandPacketId == packet.packetId }.commandResponse
    }

    suspend fun sendRequest(packet: Packet): Response {
        return writePacketWaitResponse(packet).first { it.hasResponse() && it.response.requestPacketId == packet.packetId }.response
    }

    suspend fun sendOperation(packet: Packet): OperationResponse {
        return writePacketWaitResponse(packet).first { it.hasOperationResponse() && it.operationResponse.operationPacketId == packet.packetId }.operationResponse
    }
}