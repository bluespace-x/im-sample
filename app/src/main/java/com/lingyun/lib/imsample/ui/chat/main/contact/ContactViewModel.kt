package com.lingyun.lib.imsample.ui.chat.main.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingyun.lib.component.plugin.PluginManager
import com.lingyun.lib.im.api.GroupService
import com.lingyun.lib.im.api.IMMessageService
import com.lingyun.lib.im.api.IMPlugin
import com.lingyun.lib.user.api.UserPlugin
import com.lingyun.lib.user.api.UserService
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async

/*
* Created by mc_luo on 7/14/21 3:13 PM.
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
class ContactViewModel : ViewModel() {
    val messageService = viewModelScope.async(start = CoroutineStart.LAZY) {
        val plugin = PluginManager.getPlugin(IMPlugin::class.java)!!
        plugin.getServiceAsync(IMMessageService::class.java).await()
    }

    val groupService = viewModelScope.async(start = CoroutineStart.LAZY) {
        val plugin = PluginManager.getPlugin(IMPlugin::class.java)!!
        plugin.getServiceAsync(GroupService::class.java).await()
    }

    val userDBService = viewModelScope.async(start = CoroutineStart.LAZY) {
        val plugin = PluginManager.getPlugin(IMPlugin::class.java)!!
        plugin.getServiceAsync(com.lingyun.lib.im.api.UserService::class.java).await()
    }

    val userService = viewModelScope.async {
        PluginManager.getPlugin(UserPlugin::class.java)!!.getServiceAsync(UserService::class.java).await()
    }

    fun loadContact() = viewModelScope.async {
        val user = userService.await().userInfo()!!
        val mygroups = groupService.await().myGroups(user.id).await()

        val myFriendGroup = mygroups

    }
}