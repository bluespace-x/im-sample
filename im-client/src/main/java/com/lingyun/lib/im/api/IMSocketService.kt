package com.lingyun.lib.im.api

import com.lingyun.lib.im.IMConfig
import com.lingyun.lib.im.PacketFactory
import com.lingyun.lib.im.exception.IMException
import com.lingyun.lib.im.extensions.toIMMessage
import com.lingyun.lib.im.extensions.toMessage
import com.lingyun.lib.im.socket.IMClient
import com.lingyun.lib.im.socket.Message.Companion.newMessage
import com.lingyun.lib.im.socket.connect.ConnectListener
import com.lingyun.lib.im.socket.connect.ConnectState
import com.lingyun.lib.im.socket.user.IChannelContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import proto.message.*
import timber.log.Timber

/*
* Created by mc_luo on 4/30/21 6:09 PM.
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
class IMSocketService(val imMessageService: IMMessageService) : CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()),
        ConnectListener<IMClient> {

    //    lateinit var appDatabase: IMAppDatabase
    private val imMsgFlow = MutableSharedFlow<com.lingyun.lib.im.dao.model.Message>(replay = 16)
    private val imConnectStateFlow = MutableSharedFlow<ConnectState>(replay = 1)

    @Volatile
    private var imClient: IMClient? = null

    @Volatile
    private var connectJob: Job? = null

    fun startServiceAsync(): Deferred<Boolean> = async {
        Timber.e("startServiceAsync")
        //first cancel last connecting job
        connectJob?.cancel()
        imClient?.setConnectListener(null)
        imClient?.disconnectAsync()?.await()

        connectJob = this@IMSocketService.launch {
            repeat(Int.MAX_VALUE) {
                try {
                    val client = IMClient(this@IMSocketService, IMConfig.serverUrl, IMConfig.serverPort)

                    client.setConnectListener(this@IMSocketService)
                    imClient = client
                    client.connectAsync().await()

                    client.disconnectFuture().await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                imConnectStateFlow.emit(ConnectState.RECONNECTING)
                delay(30 * 1000)
            }
        }

        val connectState =
                imConnectStateFlow.first { it == ConnectState.CONNECT_SUCCESS || it == ConnectState.CONNECT_FAIL || it == ConnectState.DISCONNECTED }
        connectState == ConnectState.CONNECT_SUCCESS
    }

    fun savePacketAsync(ctx: IChannelContext, packet: Packet): Deferred<Any> {
        return async(start = CoroutineStart.UNDISPATCHED) {
            when {
                packet.hasImmsg() -> {
                    val imchat = packet.immsg.toIMMessage()
                    imchat.ownerId = ctx.userInfo!!.userId

                    saveIMMessageAsync(ctx, imchat)
                }
                packet.hasMsgOperationStatus() -> {
                    val msgOperationStatus = packet.msgOperationStatus
                    imMessageService.saveMessageOperationStatus(msgOperationStatus)
                    true
                }
                packet.hasPosition() -> {

                }
                else -> {

                }
            }
        }
    }

    private suspend fun saveIMMessageAsync(ctx: IChannelContext, imMsg: com.lingyun.lib.im.dao.model.Message) {
        imMsgFlow.emit(imMsg)
        imMsg.ownerId = ctx.userInfo!!.userId
        imMessageService.insertMessage(imMsg)
    }

    fun sendMessageReadAsync(seqId: Long) = async {
        val client = imClient

        val messageOperation = MessageOperation.newBuilder().also {
            it.seqId = seqId
            it.action = MessageAction.READ
        }.build()


        val packet = PacketFactory.newPacketBuild().also {
            it.msgOperation = messageOperation
            it.ackStatus = AckStatus.WAIT_ACK
        }.build()

        Timber.e("client == null || !client.isConnecteComplete():${client == null || !client.isConnecteComplete()}")
        if (client == null || !client.isConnecteComplete()) {
            throw IMException("connect lost")
        }

        val message = newMessage(packet)
        client.sendMessage(message)
        message.ackFuture?.await()
    }

    fun sendIMMessageAsync(immsg: com.lingyun.lib.im.dao.model.Message): Deferred<MessageOperation> {
        Timber.e("sendIMChatAsync: $immsg")
        val packet = PacketFactory.newPacketBuild()
                .also {
                    it.immsg = immsg.toMessage()
                    it.ackStatus = AckStatus.WAIT_ACK
                }
                .build()
        val response = Channel<Packet>(16)
        val msg = newMessage(packet, response)
        val client = imClient

        Timber.e("client == null || !client.isConnecteComplete():${client == null || !client.isConnecteComplete()}")
        if (client == null || !client.isConnecteComplete()) {
            throw IMException("connect lost")
        }
        return client.async {
            client.sendMessage(msg)
            val msgOperationPacket = msg.consumerResponse().first {
                it.hasMsgOperation() && it.msgOperation.msgPacketId == packet.packetId
            }
            msgOperationPacket.msgOperation
        }
    }

    fun openGroupChatFlow(groupId: Long): Flow<com.lingyun.lib.im.dao.model.Message> {
        return imMsgFlow.filter { it.toIdType == GroupIdType.GROUP_TYPE_VALUE && it.toId == groupId }
    }

    fun openChatGroupIdFlow(groupId: GroupId): Flow<com.lingyun.lib.im.dao.model.Message> {
        return when (groupId.idType) {
            GroupIdType.GROUP_TYPE -> {
                imMsgFlow.filter { it.toIdType == GroupIdType.GROUP_TYPE_VALUE && it.toId == groupId.id }
            }
            GroupIdType.USER_TYPE -> {
                imMsgFlow.filter { it.toIdType == GroupIdType.USER_TYPE_VALUE && it.toId == groupId.id }
            }
            else -> {
                throw IMException()
            }
        }
    }

    override fun onConnecting(client: IMClient) {
        runBlocking {
            imConnectStateFlow.emit(client.connectState().get())
        }
    }

    override fun onConnectComplete(client: IMClient, e: Throwable?) {
        runBlocking {
            imConnectStateFlow.emit(client.connectState().get())
        }
    }

    override fun onDisconnecting(client: IMClient) {
        runBlocking {
            imConnectStateFlow.emit(client.connectState().get())
        }
    }

    override fun onDisconnected(client: IMClient, e: Throwable?) {
        runBlocking {
            imConnectStateFlow.emit(client.connectState().get())
        }
    }


}