package com.lingyun.lib.im.socket.user

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import proto.message.*

/*
* Created by mc_luo on 6/2/21 5:17 PM.
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
interface IChannelHandler {

    fun sendPacketAsync(packet: Packet): Deferred<*>?
    fun sendCommandAsync(command: Command): Deferred<CommandResponse>
    fun sendRequestAsync(request: Request): Deferred<Response>
    fun sendMsgAsync(msg: Message): Deferred<MessageOperation>

    fun sendOperationAsync(operation: Operation): Deferred<OperationResponse>

    fun sendPacketWaitResponse(packet: Packet): Flow<Packet>

    fun readPacketAsFlow(): Flow<Packet>

    fun disconnectFuture(): Deferred<Boolean>
    fun disconnectAsync(case: String? = null): Deferred<*>
}