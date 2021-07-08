package com.lingyun.lib.im.api.remote

import com.google.gson.Gson
import com.lingyun.lib.im.api.remote.service.IUserService
import com.lingyun.lib.im.dao.model.User
import com.lingyun.lib.network.api.BaseResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit


/*
* Created by mc_luo on 7/5/21 2:06 PM.
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
class RemoteUserService(val retrofit: Retrofit) {

    val service = lazy { retrofit.create(IUserService::class.java) }
    val gson = Gson()

    suspend fun userInfo(userId: Long): BaseResponse<User> {
        return service.value.userInfo(userId).await()
    }

    suspend fun usersInfo(userIds: List<Long>): BaseResponse<List<User>> {
        val body: RequestBody = gson.toJson(userIds).toRequestBody(("application/json; charset=utf-8").toMediaType())
        return service.value.usersInfo(body).await()
    }

    suspend fun myFriends(): BaseResponse<List<User>> {
        return service.value.myFriends().await()
    }

    suspend fun myInfo(): BaseResponse<User> {
        return service.value.myInfo().await()
    }

    suspend fun searchUserByUserNameOrPhoneNumberOrEmailLike(searchStr: String): BaseResponse<List<User>> {
        return service.value.searchUser(searchStr).await()
    }

}