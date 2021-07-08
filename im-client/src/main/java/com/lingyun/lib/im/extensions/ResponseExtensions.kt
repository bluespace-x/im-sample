package com.lingyun.lib.im.extensions

import proto.message.*

/*
* Created by mc_luo on 2021/4/23 .
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

fun Response.isSuccess(): Boolean {
    return responseCode == ResponseCode.SUCCESS
}

fun CommandResponse.isSuccess(): Boolean {
    return responseCode == ResponseCode.SUCCESS
}

fun OperationResponse.isSuccess(): Boolean {
    return responseCode == ResponseCode.SUCCESS
}

fun <T> Response.resultValue(): T? {
    if (!hasResult()) {
        return null
    }
    return result as T
}