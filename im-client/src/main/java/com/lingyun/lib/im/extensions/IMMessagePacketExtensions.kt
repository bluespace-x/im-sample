package com.lingyun.lib.im.extensions

import com.google.protobuf.util.Timestamps
import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.im.dao.model.MessageState
import com.lingyun.lib.im.dao.model.MessageStatus
import proto.message.*
import java.sql.Date

/*
* Created by mc_luo on 2021/4/28 .
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
fun proto.message.Message.toIMMessage(): Message {

    return Message().also {
        if (sequenceId != 0L) it.seqId = sequenceId
        it.timestamp = Date(Timestamps.toMillis(timestamp))
        it.fromId = from

        it.toId = to.id
        it.toIdType = to.idTypeValue


        it.messageType = messageType.number

        when {
            hasTextMsg() -> {
                it.message = textMsg.message
                it.description = textMsg.description
            }
            hasFileMsg() -> {
                it.message = fileMsg.fileUrl
                it.description = fileMsg.description
            }
            hasImageMsg() -> {
                it.message = imageMsg.imageUrl
                it.description = imageMsg.description
            }
            hasVideoMsg() -> {
                it.message = videoMsg.videoUrl
                it.description = videoMsg.description
            }
        }
    }
}

fun Message.toMessage(): proto.message.Message {
    return proto.message.Message.newBuilder().also {
        it.timestamp = Timestamps.fromMillis(timestamp.time)
        if (seqId != -1L) it.sequenceId = seqId

        it.from = fromId
        it.to = proto.message.GroupId.newBuilder().also {
            it.id = toId
            it.idType = GroupIdType.forNumber(toIdType)
        }.build()
        it.messageType = MessageType.forNumber(messageType)
        when (it.messageType) {
            MessageType.TEXT_TYPE -> {
                it.textMsg = TextMessage.newBuilder().also {
                    it.message = message
                    if (description != null) it.description = description
                }.build()
            }
            MessageType.FILE_TYPE -> {
                it.fileMsg = FileMessage.newBuilder().also {
                    it.fileUrl = message
                    if (description != null) it.description = description
                }.build()
            }
            MessageType.IMAGE_TYPE -> {
                it.imageMsg = ImageMessage.newBuilder().also {
                    it.imageUrl = message
                    if (description != null) it.description = description
                }.build()
            }
            MessageType.AUDIO_TYPE -> {
                it.audioMsg = AudioMessage.newBuilder().also {
                    it.audioUrl = message
                    if (description != null) it.description = description
                }.build()
            }
        }
    }
        .build()
}