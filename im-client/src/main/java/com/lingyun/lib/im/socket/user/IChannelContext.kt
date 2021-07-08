package com.lingyun.lib.im.socket.user

import com.lingyun.lib.im.api.IMSocketService
import kotlinx.coroutines.CoroutineScope
import proto.message.Packet
import proto.message.ProtocolInfo
import proto.message.UserTokenInfo

/*
* Created by mc_luo on 6/2/21 5:19 PM.
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
interface IChannelContext : IChannelHandler, IChannelStateHandler {
    val accessToken: String
    var userInfo: UserTokenInfo?

    var operationStatus: ChannelOperationStatus
    val clientProtocolInfo: ProtocolInfo
    var serverProtocolInfo: ProtocolInfo?

    var coroutineScope: CoroutineScope

    fun nextPacketId(): Long
    fun newPacketBuilder(): Packet.Builder

    fun imService(): IMSocketService

    fun changeChannelStateToAccessState()
    fun changeChannelStateToInit()
    fun changeChannelStateToDate()

}