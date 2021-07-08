package com.lingyun.lib.im.dao

import androidx.room.TypeConverter
import com.lingyun.lib.im.dao.model.MessageState
import java.sql.Date

/*
* Created by mc_luo on 6/7/21 3:31 PM.
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
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun fromMesssageState(messageState: MessageState): String {
        return messageState.name
    }

    fun toMessageState(messageStateValue: String): MessageState {
        return enumValueOf<MessageState>(messageStateValue)
    }
}
