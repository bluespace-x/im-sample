package com.lingyun.lib.im

import proto.message.ProtocolInfo
import proto.message.UserTokenInfo
import java.util.concurrent.atomic.AtomicLong

/*
* Created by mc_luo on 5/1/21 7:08 PM.
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
object IMConfig {
    var dom: String = "bluespace-x"
    const val protocolNumber = 1
    var serverUrl = "localhost"
    var serverPort = 50052
    var applicationName: String = ""
    var applicationVersion: String = ""
    const val platform = "Android"

    var channel: String = ""
    var device: String = ""

    var userToken: String = ""


    var tokenProvider: String? = null
    var lastPacketId = AtomicLong(0)

    var userTokenInfo: UserTokenInfo? = null

    fun clientProtocolInfo(): ProtocolInfo {
        return ProtocolInfo.newBuilder()
                .also {
                    it.application = applicationName
                    it.version = applicationVersion
                    it.platform = platform
                    it.device = device
                    it.protocolNumber = protocolNumber
                }
                .build()
    }

}