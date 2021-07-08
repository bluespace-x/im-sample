package com.lingyun.lib.im.dao.dao

import androidx.room.*
import com.lingyun.lib.im.dao.model.Group

/*
* Created by mc_luo on 6/30/21 1:59 PM.
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
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Update
    suspend fun updateGroup(group: Group)

    @Query("select * from _group where group_id = :groupId")
    suspend fun findbyGroupId(groupId: Long): Group?

    @Query("select * from _group where group_id in (:groupIds)")
    suspend fun findAllByGroupIdIn(groupIds: List<Long>): List<Group>


}