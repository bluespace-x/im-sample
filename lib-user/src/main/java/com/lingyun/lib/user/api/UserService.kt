package com.lingyun.lib.user.api

import com.lingyun.lib.network.api.BaseResponse
import com.lingyun.lib.user.IUserGroupService
import com.lingyun.lib.user.IUserService
import com.lingyun.lib.user.model.GroupMembers
import com.lingyun.lib.user.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import retrofit2.Retrofit

/*
* Created by mc_luo on 5/19/21 1:51 PM.
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
class UserService(coroutineScope: CoroutineScope, retrofit: Retrofit) : IUserService,
    IUserGroupService,
    CoroutineScope by coroutineScope {

    val userService =
        async(start = CoroutineStart.LAZY) { retrofit.create(IUserService::class.java) }

    val userGroupService =
        async(start = CoroutineStart.LAZY) { retrofit.create(IUserGroupService::class.java) }

    private var userInfo: User? = null

    override fun me(): Deferred<BaseResponse<User>> {
        return async {
            val service = userService.await()
            val response = service.me().await()

            if (response.isSuccess()) {
                userInfo = response.result
            }

            response
        }
    }

    override fun getUserInfo(userId: Long): Deferred<BaseResponse<User>> {
        return async {
            val service = userService.await()
            service.getUserInfo(userId).await()
        }
    }

    override fun groupMembers(groupId: Long): Deferred<BaseResponse<GroupMembers>> {
        return async {
            val service = userGroupService.await()
            service.groupMembers(groupId).await()
        }
    }

    override fun addGroupMember(groupId: Long, userId: Long): Deferred<BaseResponse<Any>> {
        return async {
            val service = userGroupService.await()
            service.addGroupMember(groupId, userId).await()
        }
    }

    override fun removeGroupMemeber(groupId: Long, userId: Long): Deferred<BaseResponse<Any>> {
        return async {
            val service = userGroupService.await()
            service.removeGroupMemeber(groupId, userId).await()
        }
    }

    fun userInfo() = userInfo
}