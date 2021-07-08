package com.lingyun.lib.network.api

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/*
* Created by mc_luo on 2020/10/9.
* Copyright (c) 2020 The LingYun Authors. All rights reserved.
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
const val SUCCESS_CODE = 200
const val SUCCESS_CODE_OLD = 0
const val FAIL_CODE = 500

@Serializable
@Keep
data class BaseResponse<T>(val timestamp: Long, val code: Int, val message: String?, val result: T?) {
    fun isSuccess(): Boolean {
        return code == SUCCESS_CODE || code == SUCCESS_CODE_OLD
    }
}