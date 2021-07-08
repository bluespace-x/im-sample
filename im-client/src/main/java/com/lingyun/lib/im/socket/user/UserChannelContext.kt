package com.lingyun.lib.im.socket.user

import com.google.protobuf.util.Timestamps
import com.lingyun.lib.im.IMConfig
import com.lingyun.lib.im.api.IMSocketService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import proto.message.AckStatus
import proto.message.Packet
import proto.message.ProtocolInfo
import proto.message.UserTokenInfo
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

/*
* Created by mc_luo on 6/2/21 5:49 PM.
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
class UserChannelContext(
        override val accessToken: String,
        override val clientProtocolInfo: ProtocolInfo,
        val imService: IMSocketService,
        channelHandler: IChannelHandler
) : IChannelContext, IChannelHandler by channelHandler {

    override var operationStatus: ChannelOperationStatus = ChannelOperationStatus.EXCHANGE_PROTOCOL

    override var userInfo: UserTokenInfo? = null
    override var serverProtocolInfo: ProtocolInfo? = null

    override lateinit var coroutineScope: CoroutineScope

    private val packetId = AtomicLong(0)

    private var channelState: IChannelStateHandler = AccessChannelStateHandler()

    override fun nextPacketId(): Long {
        return packetId.getAndIncrement()
    }

    override fun newPacketBuilder(): Packet.Builder {
        return Packet.newBuilder()
            .also {
                it.dom = IMConfig.dom
                it.protocolNumber = clientProtocolInfo.protocolNumber
                it.timestamp = Timestamps.fromMillis(System.currentTimeMillis())
                it.packetId = nextPacketId()
                it.ackStatus = AckStatus.WAIT_ACK
            }
    }

    override fun changeChannelStateToAccessState() {
        Timber.e("changeChannelStateToAccessState")
        channelState = AccessChannelStateHandler()
        coroutineScope.launch {
            channelState.stateActive(this@UserChannelContext)
        }
    }

    override fun changeChannelStateToInit() {
        Timber.e("changeChannelStateToInit")
        coroutineScope.launch {
            channelState.stateInactive(this@UserChannelContext)
            channelState = InitChannelStateHandler()
            channelState.stateActive(this@UserChannelContext)
        }
    }

    override fun changeChannelStateToDate() {
        Timber.e("changeChannelStateToDate")
        coroutineScope.launch {
            channelState.stateInactive(this@UserChannelContext)
            channelState = DateChannelStateHandler()
            channelState.stateActive(this@UserChannelContext)
        }
    }

    override suspend fun handlePacketRead(ctx: IChannelContext, packet: Packet) {
        when {
            packet.hasOperationStatus() -> {
                val operationStatus = packet.operationStatus
                if (operationStatus.obj == "im-channel") {
                    ctx.operationStatus =
                        buildChannelOperationStatusByValue(operationStatus.operationStatus)
                }
            }
        }
        channelState.handlePacketRead(ctx, packet)
    }

    override suspend fun stateActive(ctx: IChannelContext) {
        channelState.stateActive(ctx)
    }

    override suspend fun stateInactive(ctx: IChannelContext) {
        channelState.stateInactive(ctx)
    }

    override fun imService(): IMSocketService {
        return imService
    }
}