package com.lingyun.lib.im.dao.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/*
* Created by mc_luo on 6/17/21 4:38 PM.
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
@Entity(indices = arrayOf(Index(value = ["group_id", "user_id"], unique = true)))
class GroupUser : BaseEntity() {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("local_id")
    var id: Long? = null

    @ColumnInfo(name = "group_id")
    var groupId: Long = 0

    @ColumnInfo(name = "group_type")
    var groupType: Int = 0

    @ColumnInfo(name = "user_id")
    var userId: Long = 0

    @ColumnInfo(name = "group_role")
    var groupRole: String = "user"

    @ColumnInfo(name = "user_alias")
    var userAlias: String? = null
}