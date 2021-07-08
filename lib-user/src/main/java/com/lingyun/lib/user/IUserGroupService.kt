package com.lingyun.lib.user

import com.lingyun.lib.network.api.BaseResponse
import com.lingyun.lib.user.model.GroupMembers
import kotlinx.coroutines.Deferred
import retrofit2.http.*

/*
* Created by mc_luo on 5/19/21 10:49 AM.
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
interface IUserGroupService {

    @GET("/usergroup/{groupId}/members/all")
    fun groupMembers(@Path("groupId") groupId: Long): Deferred<BaseResponse<GroupMembers>>

    @POST("usergroup/{groupId}/members/add")
    @FormUrlEncoded
    fun addGroupMember(
        @Path("groupId") groupId: Long,
        @Field("userId") userId: Long
    ): Deferred<BaseResponse<Any>>

    @POST("usergroup/{groupId}/members/remove")
    @FormUrlEncoded
    fun removeGroupMemeber(
        @Path("groupId") groupId: Long,
        @Field("userId") userId: Long
    ): Deferred<BaseResponse<Any>>

}