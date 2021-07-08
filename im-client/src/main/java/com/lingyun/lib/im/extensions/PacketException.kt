package com.lingyun.lib.im.extensions

import com.google.protobuf.util.Timestamps
import proto.message.Packet
import java.sql.Timestamp

/*
* Created by mc_luo on 5/26/21 10:55 AM.
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
fun Packet.toPrettyString(): String {
    val time = Timestamp(Timestamps.toMillis(timestamp))
    val head = "$time dom:${dom} packetId:$packetId aks:$ackStatus"
    return when {
        hasPong() -> {
            "Pong $head"
        }
        hasPing() -> {
            "Ping $head"
        }
        hasImmsg() -> {
            val msg = when {
                immsg.hasTextMsg() -> {
                    "seqId: ${immsg.sequenceId} text: [${immsg.textMsg.message}]"
                }
                immsg.hasFileMsg() -> {
                    "seqId: ${immsg.sequenceId} file: [${immsg.textMsg.message}]"
                }
                immsg.hasImageMsg() -> {
                    "seqId: ${immsg.sequenceId} img: [${immsg.textMsg.message}]"
                }
                immsg.hasAudioMsg() -> {
                    "seqId: ${immsg.sequenceId} audio: [${immsg.textMsg.message}]"
                }
                immsg.hasVideoMsg() -> {
                    "seqId: ${immsg.sequenceId} video: [${immsg.textMsg.message}]"
                }
                else -> {
                    immsg.toString()
                }
            }
            "Message: $head $msg"
        }
        hasProtocolInfo() -> {
            toString()
        }
        hasCommand() -> {
            val cmd = "dom:${command.dom} obj:${command.obj} act:${command.act}  code:${command.commandCode}"
            "Command: $head command:[$cmd]"
        }
        hasRequest() -> {
            "Request: ${request.toString()}"
        }
        hasOperation() -> {
            "Operation: $operation"
        }
        hasSettings() -> {
            "Settings: $settings"
        }
        hasResponse() -> {
            "Response: $response"
        }
        hasAck() -> {
            "Ack: $head ackPacketId:${ack.ackPacketId}"
        }
        else -> {
            toString()
        }
    }
}