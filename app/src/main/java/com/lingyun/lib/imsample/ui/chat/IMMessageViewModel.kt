package com.lingyun.lib.imsample.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingyun.lib.component.plugin.PluginManager
import com.lingyun.lib.im.api.IMMessageService
import com.lingyun.lib.im.ui.DefaultUser
import com.lingyun.lib.im.ui.IMessage
import com.lingyun.lib.im.ui.MessageState
import com.lingyun.lib.im.ui.MessageType
import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.im.api.IMSocketService
import com.lingyun.lib.imsample.plugin.IMPlugin
import com.lingyun.lib.user.api.UserPlugin
import com.lingyun.lib.user.api.UserService
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import proto.message.*
import timber.log.Timber
import java.sql.Date

/*
* Created by mc_luo on 5/2/21 7:25 PM.
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
class IMMessageViewModel : ViewModel() {

    private val imSocketServiceDeferred = viewModelScope.async(start = CoroutineStart.LAZY) {
        PluginManager.getPlugin(IMPlugin::class.java.simpleName)!!
                .getServiceAsync(IMSocketService::class.java).await()
    }

    private val imMessageServiceDeferred = viewModelScope.async(start = CoroutineStart.LAZY) {
        PluginManager.getPlugin(IMPlugin::class.java.simpleName)!!
                .getServiceAsync(IMMessageService::class.java).await()
    }

    private val userService = viewModelScope.async(start = CoroutineStart.LAZY) {
        PluginManager.getPlugin(UserPlugin::class.java.simpleName)!!
                .getServiceAsync(UserService::class.java).await()
    }

    fun onMessageReaded(message: IMessage) = viewModelScope.async {
        message.messageState = MessageState.READED
        val imService = imMessageServiceDeferred.await()
        imService.updateMessageRead(message.messageId.toLong()).await()

        val socketService = imSocketServiceDeferred.await()

        socketService.sendMessageReadAsync(message.messageId.toLong()).await()
    }


    fun sendMessageAsync(message: IMessage, to: GroupId): Deferred<Any> {
        return viewModelScope.async {
            when (message) {
                is IMessage.TextMessage -> {
                    sendTextMessage(message, to)
                }
                is IMessage.FileMessage -> {

                }
                is IMessage.ImageMessage -> {

                }
                is IMessage.RadioMessage -> {

                }
                is IMessage.VideoMessage -> {

                }
                else -> {
                    Timber.e("send unknow message:$message")
                }
            }

        }
    }

    fun updateMessageReadedAsync(seqId: Long) = viewModelScope.async {
        val service = imSocketServiceDeferred.await()

        service.sendMessageReadAsync(seqId)
    }

    private suspend fun sendTextMessage(message: IMessage.TextMessage, to: GroupId) {
        val userInfo = userService.await().userInfo()!!
        val imChat = Message().also {
            it.fromId = userInfo.id
            it.toId = to.id
            it.toIdType = to.idTypeValue

            it.message = message.message
            it.messageType = proto.message.MessageType.TEXT_TYPE_VALUE

            it.timestamp = Date(System.currentTimeMillis())
        }

        val service = imSocketServiceDeferred.await()
        service.sendIMMessageAsync(imChat).await()
    }

    suspend fun subscribeMessage(chatGroupId: proto.message.GroupId): Flow<IMessage> {
        return imSocketServiceDeferred.await().openChatGroupIdFlow(chatGroupId).transform { msg ->
            when (msg.messageType) {
                proto.message.MessageType.TEXT_TYPE_VALUE -> {
                    val user = DefaultUser(msg.fromId.toString(), "---", null)

                    val imessage: IMessage =
                            IMessage.TextMessage(
                                    msg.seqId.toString(),
                                    MessageType.RECEIVE_TEXT,
                                    msg.timestamp.time,
                                    user,
                                    msg.message,
                                    messageState = MessageState.RECEIVERED
                            )
                    emit(imessage)
                }
                else -> {
                    throw IllegalArgumentException("unsupport this message type:${msg.messageType}")
                }
            }
        }
    }

    /**
     * 监听Message数据变化
     *
     * @param messageId
     * @return
     */
    suspend fun subscribeMessageChanged(messageId: String): Flow<IMessage> {
        val service = imMessageServiceDeferred.await()
        return service.getMessageFlow(messageId.toLong()).await().map { msg ->
            when (msg.messageType) {
                proto.message.MessageType.TEXT_TYPE_VALUE -> {
                    val user = DefaultUser(msg.fromId.toString(), "---", null)
                    val imessage: IMessage =
                            IMessage.TextMessage(
                                    msg.seqId.toString(),
                                    MessageType.RECEIVE_TEXT,
                                    msg.timestamp.time,
                                    user,
                                    msg.message,
                                    messageState = MessageState.RECEIVERED
                            )
                    imessage
                }
                else -> {
                    throw IllegalArgumentException("unsupport this message type:${msg.messageType}")
                }
            }
        }
    }
}