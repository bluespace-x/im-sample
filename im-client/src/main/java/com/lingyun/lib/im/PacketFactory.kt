package com.lingyun.lib.im

import com.google.protobuf.util.Timestamps
import proto.message.Packet
import proto.message.ProtocolInfo

/*
* Created by mc_luo on 5/1/21 7:29 PM.
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
object PacketFactory {

    fun newPacketBuild(): Packet.Builder {
        return Packet.newBuilder()
            .also {
                it.dom = IMConfig.dom
                it.protocolNumber = IMConfig.protocolNumber
                it.timestamp = Timestamps.fromMillis(System.currentTimeMillis())
                it.packetId = IMConfig.lastPacketId.getAndIncrement()
            }
    }

    fun newProtocolInfo(): ProtocolInfo {
        return ProtocolInfo.newBuilder()
            .also {
                it.application = IMConfig.applicationName
                it.version = IMConfig.applicationVersion
                it.platform = IMConfig.platform
                it.device = IMConfig.device
            }
            .build()
    }
}