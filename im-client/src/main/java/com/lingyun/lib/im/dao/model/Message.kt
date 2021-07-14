package com.lingyun.lib.im.dao.model

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.sql.Date

/*
* Created by mc_luo on 4/30/21 5:21 PM.
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
@Entity(
        tableName = "im_message", indices = [Index("seq_id")]
)
class Message : BaseEntity() {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("local_id")
    var id: Long? = null

    @ColumnInfo(name = "seq_id")
    var seqId: Long = -1L

    @ColumnInfo(name = "owner_id")
    @Transient
    var ownerId: Long = 0

    var timestamp: Date = Date(0)

    @ColumnInfo(name = "from_id")
    var fromId: Long = 0

    @ColumnInfo(name = "to_id")
    var toId: Long = 0

    @ColumnInfo(name = "to_id_type")
    var toIdType: Int = 0

    @ColumnInfo(name = "message_type")
    var messageType: Int = 0

    @ColumnInfo(name = "message_state")
    var messageState: MessageState = MessageState.RECEIVERED

    var message: String = ""

    var description: String? = null

    var data: ByteArray? = null

    @Embedded
    var messageStatus: MessageStatus = MessageStatus()
}