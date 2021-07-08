package com.lingyun.lib.im.api.remote

import com.lingyun.lib.im.api.remote.service.IGroupService
import com.lingyun.lib.im.dao.model.Group
import com.lingyun.lib.im.dao.model.GroupUser
import com.lingyun.lib.network.api.BaseResponse
import retrofit2.Retrofit

/*
* Created by mc_luo on 7/5/21 2:01 PM.
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
class RemoteGroupService(val retrofit: Retrofit) {

    val service = lazy { retrofit.create(IGroupService::class.java) }

    suspend fun groupMembers(groupId: Long): BaseResponse<List<GroupUser>> {
        return service.value.groupMembers(groupId).await()
    }

    suspend fun myGroups(): BaseResponse<List<Group>> {
        return service.value.myGroups().await()
    }

    suspend fun getUserGroups(userId: Long): BaseResponse<List<Group>> {
        return service.value.getUserGroups(userId).await()
    }

    suspend fun getGroup(groupId: Long): BaseResponse<Group> {
        return service.value.getGroup(groupId).await()
    }

}