package com.lingyun.lib.im.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.lingyun.lib.im.dao.dao.GroupDao
import com.lingyun.lib.im.dao.dao.GroupUserDao
import com.lingyun.lib.im.dao.dao.MessageDao
import com.lingyun.lib.im.dao.dao.UserDao
import com.lingyun.lib.im.dao.model.Group
import com.lingyun.lib.im.dao.model.GroupUser
import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.im.dao.model.User

/*
* Created by mc_luo on 5/1/21 8:41 PM.
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
@Database(
    version = 1,
    entities = arrayOf(Message::class, Group::class, GroupUser::class, User::class)
)
@TypeConverters(Converters::class)
abstract class IMAppDatabase : RoomDatabase() {
    abstract fun immsgDao(): MessageDao

    abstract fun groupDao(): GroupDao

    abstract fun groupUserDao(): GroupUserDao

    abstract fun userDao(): UserDao

}