package com.lingyun.lib.im.api.local

import com.lingyun.lib.im.dao.dao.MessageDao
import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.im.dao.model.MessageState
import kotlinx.coroutines.flow.Flow
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
class LocalMessageService(val messageDao: MessageDao) {

    suspend fun saveMessage(message: Message) {
        messageDao.insertMessage(message)
    }

    suspend fun updateMessage(message: Message) {
        messageDao.updateMessage(message)
    }

    suspend fun updateMessageStatus(seqId: Long,
                                    receiverCount: Int,
                                    haveReadCount: Int,
                                    revocationTime: Date?) {
        messageDao.updateMsgStatus(seqId, receiverCount, haveReadCount, revocationTime)
    }

    suspend fun updateMessageReaded(seqId: Long) {
        messageDao.updateMessageState(seqId, MessageState.READED)
    }

    suspend fun updateMesssageRevocation(seqId: Long, revocationTime: Date) {
        messageDao.updateMessageStateAndRevocationTimeBySeqId(seqId, MessageState.REVOCATION, revocationTime)
    }

    suspend fun selectUnreadCountByUserIdAndToId(userId: Long, toId: Long, toIdType: Int): Long {
        return messageDao.selectCountByUserIdAndToIdAndMessageState(userId, toId, toIdType, MessageState.RECEIVERED)
    }

    suspend fun existsByOwnerIdAndToIdAndCreateTimeBefore(ownerId: Long, toId: Long, toIdType: Int, createTimeBefore: Long): Boolean {
        return messageDao.existMessageByByOwnerIdAndToIdAndCreateTimeBefore(ownerId, toId, toIdType, createTimeBefore)
    }

    suspend fun getLatestMessageByOwnerIdAndToIdAndToType(ownerId: Long, toId: Long, toIdType: Int, limit: Int): List<Message> {
        return messageDao.getLatestMessageByOwnerIdAndToIdAndToType(ownerId, toId, toIdType, limit)
    }

    suspend fun getLatestGroupMessages(ownerId: Long): Flow<List<Message>> {
        return messageDao.getLatestMessageGroupByToId(ownerId)
    }

    suspend fun getMessageByGroupIdAndCreateTimeBefore(ownerId: Long, groupId: Long, groupIdType: Int, createTimeBefore: Long, limit: Int): List<Message> {
        return messageDao.getAllMessageByOwnerIdAndToIdAndCreateTimeBeforeAndLimit(ownerId, groupId, groupIdType, createTimeBefore, limit)
    }

    suspend fun getMessageFlow(seqId: Long): Flow<Message> {
        return messageDao.getMessageFlow(seqId)
    }

}