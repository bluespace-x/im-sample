package com.lingyun.lib.im.socket.user

import com.lingyun.lib.im.IMConfig
import com.lingyun.lib.im.PacketFactory
import com.lingyun.lib.im.extensions.isSuccess
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import proto.message.AckStatus
import proto.message.Packet
import proto.message.Request
import proto.message.UserTokenInfo
import timber.log.Timber

/*
* Created by mc_luo on 6/2/21 5:53 PM.
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
class AccessChannelStateHandler : IChannelStateHandler {

    private val accessTokenRequest = Request.newBuilder()
            .also {
                it.obj = IMConfig.userToken
                it.dom = IMConfig.dom
                it.act = "oauth_token"
            }
            .build()

    private val protocolInfoPacket = PacketFactory.newPacketBuild()
            .also {
                it.ackStatus = AckStatus.NO_ACK
                it.protocolInfo = PacketFactory.newProtocolInfo()
            }
            .build()


    override suspend fun stateActive(ctx: IChannelContext) {
        val serverProtocolInfo = withTimeoutOrNull(6000) {
            val serverProtocolInfoPacket =
                    ctx.sendPacketWaitResponse(protocolInfoPacket).first { it.hasProtocolInfo() }

            serverProtocolInfoPacket.protocolInfo
        }

        if (serverProtocolInfo == null) {
            Timber.e("timeout for get server protocol info")
            ctx.disconnectAsync("timeout for get server protocol info")
            return
        }
        ctx.serverProtocolInfo = serverProtocolInfo

        val tokenResponse = withTimeoutOrNull(6000) {
            ctx.sendRequestAsync(accessTokenRequest).await()
        }

        if (tokenResponse == null) {
            Timber.e("timeout for access token response")
            ctx.disconnectAsync("timeout for access token response").await()
            return
        }

        if (!tokenResponse.isSuccess()) {
            Timber.e("access token response fail :$tokenResponse")
            ctx.disconnectAsync("access token invaild")
            return
        }
        ctx.userInfo = tokenResponse.result.unpack(UserTokenInfo::class.java)
        IMConfig.userTokenInfo = ctx.userInfo
        ctx.changeChannelStateToInit()
    }

    override suspend fun stateInactive(ctx: IChannelContext) {

    }

    override suspend fun handlePacketRead(ctx: IChannelContext, packet: Packet) {

    }
}