package com.lingyun.lib.user.model

import kotlinx.serialization.Serializable

/*
* Created by mc_luo on 5/19/21 11:01 AM.
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
@Serializable
data class User(
    val id: Long,
    val dom: String,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val userName: String,
    val avatarUrl: String?,
    val countryCode:String?,
    val phoneNumber: String?,
    val email: String?,
    val sex: Int,
) {
}