package com.lingyun.lib.imsample.plugin

import android.app.Application
import androidx.room.Room
import com.lingyun.lib.component.plugin.AbstractPlugin
import com.lingyun.lib.component.plugin.PluginContext
import com.lingyun.lib.component.plugin.PluginManager
import com.lingyun.lib.im.IMConfig
import com.lingyun.lib.im.dao.IMAppDatabase
import com.lingyun.lib.im.api.IMSocketService
import com.lingyun.lib.network.api.NetworkPlugin
import com.lingyun.lib.network.api.OauthService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import timber.log.Timber

/*
* Created by mc_luo on 5/14/21 9:38 AM.
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
class IMPlugin : AbstractPlugin(
    CoroutineScope(Dispatchers.IO + SupervisorJob()),
    IMPlugin::class.java.simpleName
) {

    override suspend fun config(context: PluginContext) {
        Timber.e("config")
        val app = context.getValue<Application>(Application::class.java.simpleName)

        val imAppDatabase = Room.databaseBuilder(app, IMAppDatabase::class.java, "imdb")
            .build()

        val imService = IMSocketService().also { it.appDatabase = imAppDatabase }

        registerService(IMSocketService::class.java, imService)
    }

    override fun executeAsync(context: PluginContext): Deferred<Any> {
        Timber.e("executeAsync")
        return async {
            val oauthService = PluginManager.getPlugin(NetworkPlugin::class.java.simpleName)!!
                .getServiceAsync(OauthService::class.java).await()

            this@IMPlugin.launch {
                oauthService.subscribeOauthTokenInfo().filter { it.isAccessTokenAvail() }.collect {
                    IMConfig.userToken = it.accessToken
                }
            }
            val oauthTokenInfo = oauthService.awaitOauthTokenAvail()
            IMConfig.userToken = oauthTokenInfo.accessToken
            getConfigService(IMSocketService::class.java).startServiceAsync().await()
            Timber.e("executeAsync finish")
        }
    }
}