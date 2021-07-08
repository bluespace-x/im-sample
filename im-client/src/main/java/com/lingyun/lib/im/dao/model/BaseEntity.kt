package com.lingyun.lib.im.dao.model

import androidx.room.ColumnInfo
import java.io.Serializable

/*
* Created by mc_luo on 4/30/21 5:28 PM.
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
open class BaseEntity : Serializable {

    @ColumnInfo(name = "dom")
    var dom: String = ""

    // 创建时间
    @ColumnInfo(name = "create_time")
    var createTime: Long = 0

    // 最后修改时间
    @ColumnInfo(name = "update_time")
    @Transient
    var updateTime: Long = 0

    @ColumnInfo(name = "delete_time")
    @Transient
    var deleteTime: Long? = null
}