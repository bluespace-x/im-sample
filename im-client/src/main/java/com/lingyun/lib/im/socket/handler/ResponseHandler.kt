package com.lingyun.lib.im.socket.handler

import com.lingyun.lib.im.socket.Message
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import kotlinx.coroutines.*
import proto.message.Packet

/*
* Created by mc_luo on 2021/3/20 .
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
class ResponseHandler : ChannelDuplexHandler() {
    private lateinit var coroutineScope: CoroutineScope
    private val waitResponseMsgs = ArrayList<Message>()

    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
//        coroutineScope = CoroutineScope(ctx.executor().asCoroutineDispatcher() + SupervisorJob())
        coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        coroutineScope.cancel()

    }

    override fun channelRead(ctx: ChannelHandlerContext?, msg: Any) {
        require(msg is Packet)

        val toRemoves = waitResponseMsgs.filter { it.isClose.get() }
        waitResponseMsgs.removeAll(toRemoves)

        waitResponseMsgs.filter { !it.isClose.get() }
            .forEach {
                try {
                    it.acceptResponse(msg)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        super.channelRead(ctx, msg)
    }

    override fun write(ctx: ChannelHandlerContext?, msg: Any, promise: ChannelPromise?) {
        require(msg is Message)
        if (msg.needResponse()) {
            waitResponseMsgs.add(msg)
        }
        super.write(ctx, msg, promise)
    }
}