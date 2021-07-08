package com.lingyun.lib.im.socket.handler

import com.lingyun.lib.im.extensions.toPrettyString
import com.lingyun.lib.im.socket.Message
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder
import timber.log.Timber

/*
* Created by mc_luo on 5/1/21 8:00 PM.
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
class MessageEncoder() : MessageToMessageEncoder<Message>() {

    override fun encode(ctx: ChannelHandlerContext?, msg: Message, out: MutableList<Any>) {
        Timber.e(msg.packet.toPrettyString())
        out.add(msg.packet)
    }
}