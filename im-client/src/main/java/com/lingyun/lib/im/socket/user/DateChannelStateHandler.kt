package com.lingyun.lib.im.socket.user

import proto.message.MessageAction
import proto.message.MessageOperation
import proto.message.Packet

/*
* Created by mc_luo on 6/2/21 6:07 PM.
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
class DateChannelStateHandler : IChannelStateHandler {

    override suspend fun handlePacketRead(ctx: IChannelContext, packet: Packet) {
        ctx.imService().savePacketAsync(ctx, packet).await()
        val userId = ctx.userInfo!!.userId
        when {
            packet.hasImmsg() -> {
                val immsg = packet.immsg

                val msgOperationPacket = ctx.newPacketBuilder()
                        .also {
                            it.msgOperation = MessageOperation.newBuilder()
                                    .also {
                                        it.seqId = immsg.sequenceId
                                        it.msgPacketId = packet.packetId
                                        it.operationId = userId
                                        it.userId = userId
                                        it.action = MessageAction.RECEIVER
                                    }
                                    .build()
                        }.build()

                ctx.sendPacketAsync(msgOperationPacket)
            }
        }
    }

    override suspend fun stateActive(ctx: IChannelContext) {

    }

    override suspend fun stateInactive(ctx: IChannelContext) {

    }
}