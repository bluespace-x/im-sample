package com.lingyun.lib.im.api.local

import com.lingyun.lib.im.dao.IMAppDatabase
import com.lingyun.lib.im.dao.model.Group
import com.lingyun.lib.im.dao.model.GroupUser
import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.im.dao.model.User
import kotlinx.coroutines.flow.Flow


/*
* Created by mc_luo on 6/30/21 2:39 PM.
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
class LocalGroupService(val db: IMAppDatabase) {

    val groupDao = db.groupDao()
    val groupUserDao = db.groupUserDao()
    val userDao = db.userDao()

    suspend fun findAllGroupByUserId(userId: Long): List<Group> {
        val groupIds = groupUserDao.findAllGroupIdByUserId(userId)
        if (groupIds.isEmpty()) return emptyList()

        val groups = groupDao.findAllByGroupIdIn(groupIds)
        return groups
    }

    suspend fun getGroupByGroupId(groupId: Long): Group? {
        return groupDao.findbyGroupId(groupId)
    }

    suspend fun findGroupUserByGroupId(groupId: Long): List<User> {
        val userIds = groupUserDao.findAllUserIdByGroupId(groupId)
        val users = userDao.findAllUserByUserIdIn(userIds)
        return users
    }

    suspend fun saveGroupUser(groupUser: GroupUser) {
        groupUserDao.insert(groupUser)
    }

    suspend fun saveGroupUsers(groupUsers: List<GroupUser>) {
        groupUserDao.insertAll(groupUsers)
    }



}