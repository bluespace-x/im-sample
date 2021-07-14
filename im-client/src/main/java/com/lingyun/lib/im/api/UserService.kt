package com.lingyun.lib.im.api

import android.content.res.Resources
import com.lingyun.lib.im.api.local.LocalUserService
import com.lingyun.lib.im.api.remote.RemoteUserService
import com.lingyun.lib.im.dao.dao.UserDao
import com.lingyun.lib.im.dao.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit

/*
* Created by mc_luo on 6/30/21 2:32 PM.
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
class UserService(val retrofit: Retrofit, val userDao: UserDao) : CoroutineScope by CoroutineScope(Dispatchers.IO) {

    val localUserService = LocalUserService(userDao)

    val remoteUserService = RemoteUserService(retrofit)


    fun getUserInfoAsync(userId: Long): Deferred<Result<User>> = async {
        val localUser = localUserService.findUserByUserId(userId)
        if (localUser != null) {
            return@async (Result.success(localUser))
        }

        try {
            val remoteUserResponse = remoteUserService.userInfo(userId)
            val result = if (remoteUserResponse.isSuccess()) {
                val remoteUser = remoteUserResponse.result!!
                localUserService.updateAsync(remoteUser)

                Result.success(remoteUserResponse.result!!)
            } else {
                Result.failure(Resources.NotFoundException())
            }
            return@async (result)
        } catch (e: Exception) {
            return@async (Result.failure(e))
        }
    }

    fun getAllUserInfo(userIds: List<Long>): Deferred<Flow<Result<List<User>>>> = async {
        return@async flow {
            val localUsers = localUserService.findAllUserByUserIdIn(userIds)

            if (localUsers != null) {
                emit(Result.success(localUsers))
            }

            try {
                val remoteResponse = remoteUserService.usersInfo(userIds)
                if (remoteResponse.isSuccess() && remoteResponse.result != null) {
                    emit(Result.success(remoteResponse.result!!))
                } else {
                    emit(Result.failure<List<User>>(Resources.NotFoundException()))
                }
            } catch (e: Exception) {
                emit(Result.failure<List<User>>(e))
            }

        }
    }


}