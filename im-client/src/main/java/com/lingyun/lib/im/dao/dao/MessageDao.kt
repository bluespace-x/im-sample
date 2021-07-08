package com.lingyun.lib.im.dao.dao

import androidx.room.*
import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.im.dao.model.MessageState
import kotlinx.coroutines.flow.Flow
import proto.message.MessageStatus
import java.sql.Date

/*
* Created by mc_luo on 4/30/21 5:47 PM.
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
@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(imMsg: Message)

    @Update
    suspend fun updateMessage(imMsg: Message)

    @Query("update im_message set receiver_count =:receiverCount and have_read_count = :haveReadCount and revocation_time =:revocationTime where seq_id =:seqId")
    suspend fun updateMsgStatus(
            seqId: Long,
            receiverCount: Int,
            haveReadCount: Int,
            revocationTime: Date?
    )

    @Query("select * from im_message where seq_id = :seqId")
    suspend fun getMessageFlow(seqId: Long): Flow<Message>

    @Query("update im_message set message_state = :messageState and revocation_time = :revocationTime where seq_id = :seqId")
    suspend fun updateMessageStateAndRevocationTimeBySeqId(seqId: Long, messageState: MessageState, revocationTime: Date)

    @Query("update im_message set message_state = :messageState where seq_id = :seqId")
    suspend fun updateMessageState(seqId: Long, messageState: MessageState)

    @Query("select * from im_message where seq_id = :seqId")
    suspend fun getMessageBySeqId(seqId: Long): Message?

    @Query("select count(seq_id) from im_message where owner_id = :ownerId AND message_state = :msgState order by create_time")
    suspend fun selectCountByOwnerIdAndMessageState(ownerId: Long, msgState: MessageState)

    @Query("select count(seq_id) from im_message where owner_id = :ownerId AND to_id = :toId AND to_id_type = :toIdType and message_state =:messageState")
    suspend fun selectCountByUserIdAndToIdAndMessageState(
            ownerId: Long,
            toId: Long,
            toIdType: Int,
            messageState: MessageState
    ): Long

    @Query("select * from im_message where owner_id = :ownerId AND to_id = :toId AND to_id_type = :toIdType")
    suspend fun getAllMessageByOwnerIdAndToId(ownerId: Long, toId: Long, toIdType: Int): List<Message>

    @Query("select * from im_message where owner_id = :ownerId AND to_id = :toId AND to_id_type = :toIdType and create_time > :createTimeAfter")
    suspend fun getAllMessageByOwnerIdAndToIdAndCreateTimeAfter(
            ownerId: Long, toId: Long, toIdType: Int, createTimeAfter: Long
    ): List<Message>

    @Query("select * from im_message where owner_id = :ownerId AND to_id = :toId AND to_id_type = :toIdType and create_time < :createTimeBefore limit :limit")
    suspend fun getAllMessageByOwnerIdAndToIdAndCreateTimeBeforeAndLimit(
            ownerId: Long, toId: Long, toIdType: Int, createTimeBefore: Long, limit: Int
    ): List<Message>

    @Query("select exists (select seq_id from im_message where owner_id = :ownerId AND to_id = :toId AND to_id_type = :toIdType and create_time < :createTimeBefore)")
    suspend fun existMessageByByOwnerIdAndToIdAndCreateTimeBefore(
            ownerId: Long, toId: Long, toIdType: Int, createTimeBefore: Long
    ): Boolean

    @Query("select * from im_message where owner_id = :ownerId AND to_id = :toId AND to_id_type = :toIdType and create_time between :createTimeAfter AND :createTimeBefore")
    suspend fun getAllMessageByOwnerIdAndToIdAndCreateTimeBetween(
            ownerId: Long, toId: Long, toIdType: Int, createTimeBefore: Long, createTimeAfter: Long
    ): List<Message>

    @Query("select * from im_message where owner_id = :ownerId group by to_id and to_id_type order by create_time desc")
    suspend fun getLatestMessageGroupByToId(ownerId: Long): Flow<List<Message>>

    @Query("select * from im_message where owner_id = :ownerId AND to_id = :toId AND to_id_type = :toIdType order by create_time desc limit :limit")
    suspend fun getLatestMessageByOwnerIdAndToIdAndToType(ownerId: Long, toId: Long, toIdType: Int, limit: Int): List<Message>
}