package com.lingyun.lib.im.api.remote.service

import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.network.api.BaseResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

/*
* Created by mc_luo on 7/5/21 2:14 PM.
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
interface IMessageService {

    @GET("message/unreaded")
    fun getUnreadedMessageByUserIdAndToId(userId: Long, toId: Long, toIdType: Long): Deferred<BaseResponse<Long>>

    @GET("message/timebefore")
    fun getMessageByUserIdAndToIdAndCreateTimebefore(userId: Long, toId: Long, toIdType: Int, createTimeBefore: Long, limit: Int): Deferred<BaseResponse<List<Message>>>

    @GET("message/timebetween")
    fun getMessageByUserIdAndToIdAndCreateTimebetween(userId: Long, toId: Long, toIdType: Long, createTimeBefore: Long, createTimeAfter: Long, limit: Long? = null): Deferred<BaseResponse<Message>>

    @GET("message/timeafter")
    fun getMessageByUserIdAndToIdAndCreateTimeAfter(userId: Long, toId: Long, toIdType: Long, createTimeAfter: Long, limit: Long): Deferred<BaseResponse<Message>>


}