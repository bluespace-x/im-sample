package com.lingyun.lib.im.api.remote.service

import com.lingyun.lib.im.dao.model.GroupUser
import com.lingyun.lib.im.dao.model.Group
import com.lingyun.lib.network.api.BaseResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

/*
* Created by mc_luo on 6/17/21 4:26 PM.
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
interface IGroupService {

    @GET("group/{groupId}/members")
    fun groupMembers(@Path("groupId") groupId: Long): Deferred<BaseResponse<List<GroupUser>>>

    @GET("my/group")
    fun myGroups(): Deferred<BaseResponse<List<Group>>>

    @GET("group/{groupId}/info")
    fun getGroup(@Path("groupId") groupId: Long): Deferred<BaseResponse<Group>>

    @GET("user/{userId}/groups")
    fun getUserGroups(@Path("userId") userId: Long): Deferred<BaseResponse<List<Group>>>

}