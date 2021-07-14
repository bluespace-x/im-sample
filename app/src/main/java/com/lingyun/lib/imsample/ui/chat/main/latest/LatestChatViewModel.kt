package com.lingyun.lib.imsample.ui.chat.main.latest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingyun.lib.component.plugin.PluginManager
import com.lingyun.lib.im.api.GroupService
import com.lingyun.lib.im.api.IMMessageService
import com.lingyun.lib.im.api.IMPlugin
import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.user.api.UserPlugin
import com.lingyun.lib.user.api.UserService
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import proto.message.GroupIdType

/*
* Created by mc_luo on 7/13/21 10:19 AM.
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
class LatestChatViewModel : ViewModel() {

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

    suspend fun loadGroupLatestMsg(): List<LatestMessage> {
        val userTokenInfo = userService.await().userInfo()!!
        val latestMessages = messageService.await().loadLatestMessageOrderByGroup(userTokenInfo.id).await()

        return latestMessages.map {
            val name = loadGroupName(it.toId, it.toIdType)
            LatestMessage(it.toId, it.toIdType, name, null, it.message, it.createTime)
        }
    }

    suspend fun loadLastMsgFlow(): Flow<Message?> {
        val userTokenInfo = userService.await().userInfo()!!
        return messageService.await().loadLatestMessageFlow(userTokenInfo.id).await()
    }

    suspend fun loadGroupName(groupId: Long, groupIdType: Int): String {
        val gs = groupService.await()
        val us = userDBService.await()

        return when (groupIdType) {
            GroupIdType.GROUP_TYPE_VALUE -> {
                val groupResult = gs.getGroupAsync(groupId).await()
                when {
                    groupResult.isSuccess -> {
                        groupResult.getOrNull()?.groupName!!
                    }
                    else -> {
                        "group-${groupId}"
                    }
                }

            }
            GroupIdType.USER_TYPE_VALUE -> {
                val userResult = us.getUserInfoAsync(groupId).await()

                when {
                    userResult.isSuccess -> {
                        userResult.getOrNull()?.userName!!
                    }
                    else -> {
                        "user-${groupId}"
                    }
                }
            }
            else -> {
                "${groupId}"
            }
        }

    }

}