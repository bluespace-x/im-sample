package com.lingyun.lib.im.socket.user

import proto.message.Packet

/*
* Created by mc_luo on 6/2/21 5:15 PM.
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
interface IChannelStateHandler {
    suspend fun handlePacketRead(ctx: IChannelContext, packet: Packet)
    suspend fun stateActive(ctx: IChannelContext)
    suspend fun stateInactive(ctx: IChannelContext)
}