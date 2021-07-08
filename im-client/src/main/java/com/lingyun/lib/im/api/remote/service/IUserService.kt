package com.lingyun.lib.im.api.remote.service

import com.lingyun.lib.im.dao.model.User
import com.lingyun.lib.network.api.BaseResponse
import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path

/*
* Created by mc_luo on 6/17/21 4:04 PM.
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
interface IUserService {

    @GET("user/{userId}/info")
    fun userInfo(@Path("userId") userId: Long): Deferred<BaseResponse<User>>

    @GET("users/info")
    fun usersInfo(@Body body: RequestBody): Deferred<BaseResponse<List<User>>>

    @GET("my/friends")
    fun myFriends(): Deferred<BaseResponse<List<User>>>

    @GET("my/info")
    fun myInfo(): Deferred<BaseResponse<User>>

    @GET("search/user/like/{searchStr}")
    fun searchUser(@Path("searchStr") searchStr: String): Deferred<BaseResponse<List<User>>>

}