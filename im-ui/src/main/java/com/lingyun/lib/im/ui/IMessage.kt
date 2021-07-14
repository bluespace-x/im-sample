package com.lingyun.lib.im.ui

/*
* Created by mc_luo on 5/17/21 9:56 AM.
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
sealed class IMessage(
        open var messageId: String,
        open val messageType: MessageType,
        open val timestamp: Long,
        open var messageState: MessageState,
) {

    data class TextMessage(
            override var messageId: String,
            override val messageType: MessageType,
            override val timestamp: Long,
            val user: IUser,
            val message: String,
            override var messageState: MessageState
    ) : IMessage(messageId, messageType, timestamp, messageState)

    data class ImageMessage(
            override var messageId: String,
            override val messageType: MessageType,
            override val timestamp: Long,
            val user: IUser,
            val imageUrl: String,
            override var messageState: MessageState
    ) : IMessage(messageId, messageType, timestamp, messageState)

    data class FileMessage(
            override var messageId: String,
            override val messageType: MessageType,
            override val timestamp: Long,
            val user: IUser,
            val fileUrl: String,
            override var messageState: MessageState
    ) : IMessage(messageId, messageType, timestamp, messageState)

    data class VideoMessage(
            override var messageId: String,
            override val messageType: MessageType,
            override val timestamp: Long,
            val user: IUser,
            val videoUrl: String,
            override var messageState: MessageState
    ) : IMessage(messageId, messageType, timestamp, messageState)

    data class RadioMessage(
            override var messageId: String,
            override val messageType: MessageType,
            override val timestamp: Long,
            val user: IUser,
            val radioUrl: String,
            override var messageState: MessageState
    ) : IMessage(messageId, messageType, timestamp, messageState)

    fun isReceiveMessage(): Boolean {
        return messageType.isReceiveType()
    }
}

enum class MessageType {
    SEND_TEXT, RECEIVE_TEXT, SEND_IMAGE, RECEIVE_IMAGE,
    SEND_FILE, RECEIVE_FILE, SEND_VIDEO, RECEIVE_RADIO,
    SEND_POSITION, RECEIVE_POSITION, SEND_UNKNOW, RECEIVE_UNKNOW;


    fun isReceiveType(): Boolean {
        return when (this) {
            RECEIVE_TEXT, RECEIVE_IMAGE, RECEIVE_FILE, RECEIVE_RADIO, RECEIVE_POSITION, RECEIVE_UNKNOW -> true
            else -> false
        }
    }
}

enum class MessageState {
    SENDING, SEND_SUCCESS, SEND_FAIL, RECEIVERED, READED, REVOCATION
}