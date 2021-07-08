package com.lingyun.lib.user

import com.lingyun.lib.network.api.BaseResponse
import com.lingyun.lib.user.model.User
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

/*
* Created by mc_luo on 5/19/21 10:48 AM.
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

    @GET("/api/v1/user/me")
    fun me(): Deferred<BaseResponse<User>>

    @GET("/api/v1/user/{userId}/info")
    fun getUserInfo(@Path("userId") userId: Long): Deferred<BaseResponse<User>>
}