package com.lingyun.lib.im.api

import android.content.res.Resources
import com.lingyun.lib.im.api.local.LocalGroupService
import com.lingyun.lib.im.api.remote.RemoteGroupService
import com.lingyun.lib.im.dao.IMAppDatabase
import com.lingyun.lib.im.dao.model.Group
import com.lingyun.lib.im.dao.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit

/*
* Created by mc_luo on 6/30/21 2:33 PM.
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
class GroupService(val retrofit: Retrofit, val db: IMAppDatabase) : CoroutineScope by CoroutineScope(Dispatchers.IO) {

    val localGroupService = LocalGroupService(db)
    val userService = UserService(retrofit, db.userDao())

    val remoteGroupService = RemoteGroupService(retrofit)

    fun groupMembers(groupId: Long): Deferred<Flow<Result<List<User>>>> = async {
        return@async flow<Result<List<User>>> {

            try {
                val groupMemberResponse = remoteGroupService.groupMembers(groupId)
                if (groupMemberResponse.isSuccess() && groupMemberResponse.result != null) {
                    val groupMember = groupMemberResponse.result!!

                    localGroupService.saveGroupUsers(groupMember)

                    emitAll(userService.getAllUserInfo(groupMember.map { it.userId }).await())
                } else {
                    emit(Result.failure(Resources.NotFoundException()))
                }

            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    fun getGroupAsync(groupId: Long): Deferred<Result<Group>> = async {
        val localGroup = localGroupService.getGroupByGroupId(groupId)
        if (localGroup != null) {
            return@async Result.success(localGroup)
        }

        try {
            val groupResponse = remoteGroupService.getGroup(groupId)
            if (groupResponse.isSuccess() && groupResponse.result != null) {
                val group = groupResponse.result!!
                localGroupService.saveGroup(group)
                return@async (Result.success(group))
            } else {
                return@async (Result.failure(Resources.NotFoundException()))
            }
        } catch (e: Exception) {
            return@async (Result.failure(e))
        }
    }

    fun myGroups(myUserId: Long): Deferred<Result<List<Group>>> = async {

        val localGroups = localGroupService.findAllGroupByUserId(myUserId)
        if (localGroups.isNotEmpty()) return@async (Result.success(localGroups))

        try {
            val response = remoteGroupService.myGroups()
            if (response.isSuccess() && response.result != null) {
                return@async (Result.success(response.result!!))
            } else {
                return@async (Result.failure(Resources.NotFoundException()))
            }
        } catch (e: java.lang.Exception) {
            return@async (Result.failure(e))
        }
    }
}