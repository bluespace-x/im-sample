package com.lingyun.lib.im.api.local

import com.lingyun.lib.im.dao.dao.UserDao
import com.lingyun.lib.im.dao.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

/*
* Created by mc_luo on 7/5/21 2:35 PM.
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
class LocalUserService(val userDao: UserDao) {

    @Suppress("RESULT_CLASS_IN_RETURN_TYPE", "UNCHECKED_CAST")
    suspend fun insertAsync(user: User) {
        userDao.insert(user)
    }

    suspend fun updateAsync(user: User) {
        userDao.update(user)
    }

    suspend fun findUserByUserId(userId: Long): User? {
        return userDao.findUserByUserId(userId)
    }

    suspend fun findAllUserByUserIdIn(userids: List<Long>): List<User> {
        return userDao.findAllUserByUserIdIn(userids)
    }

    suspend fun searchUserByUserNameOrPhoneNumberOrEmailLike(searchStr: String): List<User> {
        return userDao.searchByUserNameOrPhoneOrEmailLike(searchStr)
    }

}