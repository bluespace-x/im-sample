package com.lingyun.lib.im.api

import android.content.res.Resources
import com.google.protobuf.util.Timestamps
import com.lingyun.lib.im.api.local.LocalMessageService
import com.lingyun.lib.im.api.remote.RemoteMessageService
import com.lingyun.lib.im.dao.IMAppDatabase
import com.lingyun.lib.im.dao.model.Message
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import proto.message.MessageOperationStatus
import retrofit2.Retrofit
import java.sql.Date

/*
* Created by mc_luo on 7/5/21 10:02 AM.
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
class IMMessageService(val retrofit: Retrofit, val db: IMAppDatabase) : CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {

    val localService = LocalMessageService(db.immsgDao())
    val remoteService = RemoteMessageService(retrofit)

    fun insertMessage(message: Message) = async {
        localService.saveMessage(message)
    }

    fun updateMessageRead(seqId: Long) = async {
        localService.updateMessageReaded(seqId)
    }

    fun loadLatestMessageByGroupId(ownerId: Long, groupId: Long, groupIdType: Int, limit: Int) = async {
        localService.getLatestMessageByOwnerIdAndToIdAndToType(ownerId, groupId, groupIdType, limit)
    }

    fun loadLatestMessageOrderByGroup(ownerId: Long) = async {
        localService.getLatestGroupMessages(ownerId)
    }

    fun loadLatestMessageFlow(ownerId: Long) = async {
        localService.getLatestMessageFlow(ownerId)
    }

    fun loadMessageByGroupIdAndCreateTimeBefore(ownerId: Long, groupId: Long, groupIdType: Int, createTimeBefore: Long, limit: Int): Deferred<Result<List<Message>>> = async {
        val localMsg = localService.getMessageByGroupIdAndCreateTimeBefore(ownerId, groupId, groupIdType, createTimeBefore, limit)
        if (localMsg.isNotEmpty()) return@async Result.success(localMsg)

        try {
            val response = remoteService.getMessageByUserIdAndToIdAndCreateTimebefore(ownerId, groupId, groupIdType, createTimeBefore, limit).await()
            return@async if (response.isSuccess() && response.result != null) {
                Result.success(response.result!!)
            } else {
                Result.failure(Resources.NotFoundException())
            }
        } catch (e: Exception) {
            return@async Result.failure(e)
        }
    }

    fun saveMessageOperationStatus(msgOperationStatus: MessageOperationStatus) = async {
        val revocationDate = if (msgOperationStatus.hasRevocationTime()) Date(
                Timestamps.toMillis(msgOperationStatus.revocationTime)
        ) else null

        localService.updateMessageStatus(
                msgOperationStatus.seqId,
                msgOperationStatus.receiverCount,
                msgOperationStatus.haveReadCount,
                revocationDate)
    }

    fun getMessageFlow(seqId: Long): Deferred<Flow<Message?>> = async {
        return@async localService.getMessageFlow(seqId)
    }
}