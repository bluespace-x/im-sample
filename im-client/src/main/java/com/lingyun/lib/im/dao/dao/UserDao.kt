package com.lingyun.lib.im.dao.dao

import androidx.room.*
import com.lingyun.lib.im.dao.model.User

/*
* Created by mc_luo on 6/30/21 3:26 PM.
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
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("select * from im_user where id = :userId")
    suspend fun findUserByUserId(userId: Long): User?

    @Query("select * from im_user where id in (:userids)")
    suspend fun findAllUserByUserIdIn(userids: List<Long>): List<User>

    @Query("select * from im_user where user_name like :searchStr or phone_number like :searchStr or email like :searchStr")
    suspend fun searchByUserNameOrPhoneOrEmailLike(searchStr: String): List<User>

}