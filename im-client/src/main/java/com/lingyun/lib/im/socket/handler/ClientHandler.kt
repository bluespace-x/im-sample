package com.lingyun.lib.im.socket.handler

import com.lingyun.lib.im.IMConfig
import com.lingyun.lib.im.api.IMSocketService
import com.lingyun.lib.im.socket.user.IChannelHandler
import com.lingyun.lib.im.socket.user.UserChannelContext
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import proto.message.*
import timber.log.Timber

/*
* Created by mc_luo on 2021/4/19 .
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
class ClientHandler(val imService: IMSocketService) : ChannelDuplexHandler(), IChannelHandler {

    lateinit var coroutineScope: CoroutineScope

    lateinit var ctx: ChannelHandlerContext

    private var isChannelInactive = false

    val packetFlow = MutableSharedFlow<Packet>(extraBufferCapacity = 1024)

    val userContext: UserChannelContext =
        UserChannelContext(IMConfig.userToken, IMConfig.clientProtocolInfo(), imService, this)

    val disconnectFuture = CompletableDeferred<Boolean>()

    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
        Timber.e("channel active")
        this.ctx = ctx
//        coroutineScope = CoroutineScope(ctx.executor().asCoroutineDispatcher() + SupervisorJob())
        coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        userContext.coroutineScope = coroutineScope

        userContext.changeChannelStateToAccessState()
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        Timber.e("channel inactive")
        isChannelInactive = true
        disconnectFuture.complete(true)
        coroutineScope.cancel()
    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any) {
        require(msg is Packet)

        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            packetFlow.emit(msg)
            userContext.handlePacketRead(userContext, msg)
        }
    }

    override fun write(ctx: ChannelHandlerContext?, msg: Any?, promise: ChannelPromise?) {
        super.write(ctx, msg, promise)
    }

    override fun sendPacketAsync(packet: Packet): Deferred<*>? {
        val msg = com.lingyun.lib.im.socket.Message.newMessage(packet)
        ctx.writeAndFlush(msg)
        return msg.ackFuture
    }

    override fun sendPacketWaitResponse(
        packet: Packet
    ): Flow<Packet> {
        val chan = Channel<Packet>(Channel.UNLIMITED)
        val message = com.lingyun.lib.im.socket.Message.newMessage(packet, chan)

        val f = message.consumerResponse()
        ctx.writeAndFlush(message)
        return f
    }

    override fun sendCommandAsync(command: Command): Deferred<CommandResponse> {
        val packet = userContext.newPacketBuilder().also {
            it.command = command
        }.build()
        return coroutineScope.async { sendPacketWaitResponse(packet).first { it.hasCommandResponse() && it.commandResponse.commandPacketId == packet.packetId }.commandResponse }
    }

    override fun sendRequestAsync(request: Request): Deferred<Response> {
        val packet = userContext.newPacketBuilder().also {
            it.request = request
        }.build()
        return coroutineScope.async {
            sendPacketWaitResponse(packet).first { it.hasResponse() && it.response.requestPacketId == packet.packetId }.response
        }
    }

    override fun sendMsgAsync(msg: Message): Deferred<MessageOperation> {
        val packet = userContext.newPacketBuilder().also {
            it.immsg = msg
        }.build()
        return coroutineScope.async { sendPacketWaitResponse(packet).first { it.hasMsgOperation() && it.msgOperation.msgPacketId == packet.packetId }.msgOperation }
    }

    override fun sendOperationAsync(operation: Operation): Deferred<OperationResponse> {
        val packet = userContext.newPacketBuilder().also {
            it.operation = operation
        }.build()
        return coroutineScope.async { sendPacketWaitResponse(packet).first { it.hasOperationResponse() && it.operationResponse.operationPacketId == packet.packetId }.operationResponse }
    }

    override fun readPacketAsFlow(): Flow<Packet> {
        return packetFlow
    }

    override fun disconnectFuture(): Deferred<Boolean> {
        return disconnectFuture
    }

    override fun disconnectAsync(case: String?): Deferred<*> {
        ctx.close()
        return coroutineScope.async { }
    }
}
