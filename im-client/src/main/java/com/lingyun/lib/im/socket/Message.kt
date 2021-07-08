package com.lingyun.lib.im.socket

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onCompletion
import proto.message.AckStatus
import proto.message.Packet
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean

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
data class Message(
    val packet: Packet,
    val ackTimeout: Long,
    val ackRetryCount: Int,
    val ackFuture: CompletableDeferred<Boolean>?,
    private val responseChan: Channel<Packet>? = null,
    var isClose: AtomicBoolean = AtomicBoolean(false)
) {

    companion object {
        fun newMessage(packet: Packet, responseChan: Channel<Packet>? = null): Message {
            val ackf = if (packet.ackStatus == AckStatus.WAIT_ACK) CompletableDeferred<Boolean>() else null
            return Message(packet, SocketConfig.ACK_TIMEOUT, SocketConfig.ACK_RETRY_COUNT, ackf, responseChan)
        }
    }

    fun ackMessage(ackPacket: Packet): Boolean {
        ackFuture?.complete(true)
        return true
    }

    fun needResponse(): Boolean {
        return responseChan != null
    }

    fun acceptResponse(packet: Packet) {
        if (!isNeedAck() || isAckSuccess()) {
            if (responseChan != null && !responseChan.isClosedForSend) {
                val ok = responseChan.offer(packet)
                if (!ok) {
                    responseChan.sendBlocking(packet)
                }
            }
        }
    }

    fun isNeedAck(): Boolean {
        return packet.ackStatus == AckStatus.ACK
    }

    fun isAckSuccess(): Boolean {
        return ackFuture?.isCompleted == true && ackFuture.getCompletionExceptionOrNull() == null
    }

    fun close(e: Exception? = null) {
        if (e == null) {
            ackFuture?.completeExceptionally(CancellationException())
        } else {
            ackFuture?.completeExceptionally(e)
        }
        responseChan?.close(e)
        isClose.set(true)
    }

    fun consumerResponse(): Flow<Packet> {
        return responseChan!!.consumeAsFlow().onCompletion {
            close()
        }
    }
}