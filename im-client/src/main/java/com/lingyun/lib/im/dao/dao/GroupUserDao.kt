package com.lingyun.lib.im.dao.dao

import androidx.room.*
import com.lingyun.lib.im.dao.model.GroupUser

/*
* Created by mc_luo on 6/30/21 2:03 PM.
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
interface GroupUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(groupUser: GroupUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(groupUsers: List<GroupUser>)

    @Update
    suspend fun update(groupUser: GroupUser): Int

    @Query("select group_id from groupuser where user_id = :userId and group_type = :groupType")
    suspend fun findAllGroupIdByUserIdAndGroupType(userId: Long, groupType: Int): List<Long>

    @Query("select group_id from groupuser where user_id = :userId")
    suspend fun findAllGroupIdByUserId(userId: Long): List<Long>

    @Query("select user_id from groupuser where group_id = :groupId")
    suspend fun findAllUserIdByGroupId(groupId: Long): List<Long>

    @Query("select * from groupuser where group_id = :groupId")
    suspend fun findAllByGroupId(groupId: Long): List<GroupUser>
}