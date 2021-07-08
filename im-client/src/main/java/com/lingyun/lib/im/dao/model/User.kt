package com.lingyun.lib.im.dao.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
* Created by mc_luo on 5/20/21 5:10 PM.
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
@Entity(tableName = "im_user")
class User: BaseEntity(){
    @PrimaryKey
    var id: Long = 0

    @ColumnInfo(name = "first_name")
    var firstName: String? = null

    @ColumnInfo(name = "middle_name")
    var middleName: String? = null

    @ColumnInfo(name = "last_name")
    var lastName: String? = null

    @ColumnInfo(name = "user_name")
    var userName: String = ""

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String? = null

    @ColumnInfo(name = "country_code")
    var countryCode: String? = null

    @ColumnInfo(name = "phone_number")
    var phoneNumber: String? = null

    @ColumnInfo(name = "email")
    var email: String? = null

    var sex: Int = 0
}