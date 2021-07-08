package com.lingyun.lib.im.ui

/*
* Created by mc_luo on 5/17/21 10:00 AM.
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
interface IUser {
    val userId: String
    val userName: String
    val avatarUrl: String?
}

data class DefaultUser(
    override val userId: String,
    override val userName: String,
    override val avatarUrl: String?
) : IUser