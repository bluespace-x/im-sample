package com.lingyun.lib.im.api

import android.app.Application
import androidx.room.Room
import com.lingyun.lib.component.plugin.AbstractPlugin
import com.lingyun.lib.component.plugin.PluginContext
import com.lingyun.lib.component.plugin.PluginManager
import com.lingyun.lib.im.dao.IMAppDatabase
import com.lingyun.lib.network.api.NetworkPlugin
import kotlinx.coroutines.*

/*
* Created by mc_luo on 7/6/21 11:00 AM.
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
class IMPlugin() : AbstractPlugin(CoroutineScope(Dispatchers.IO + SupervisorJob()), IMPlugin::class.java.simpleName) {


    lateinit var socketService: IMSocketService
    override suspend fun config(context: PluginContext) {
        val application = context.getValue<Application>(Application::class.java.simpleName)
        val db = Room.databaseBuilder(application, IMAppDatabase::class.java, "db")
                .build()

        val retrofit = PluginManager.getPlugin(NetworkPlugin::class.java)!!.retrofit

        val userService = UserService(retrofit, db.userDao())
        val groupService = GroupService(retrofit, db)
        val messageService = IMMessageService(retrofit, db)

        socketService = IMSocketService(messageService)

        registerService(IMMessageService::class.java, messageService)
        registerService(IMSocketService::class.java, socketService)
        registerService(UserService::class.java, userService)
        registerService(GroupService::class.java, groupService)
    }

    override fun executeAsync(context: PluginContext): Deferred<Any> = async {

    }
}