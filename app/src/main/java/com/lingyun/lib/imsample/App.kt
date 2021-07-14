package com.lingyun.lib.imsample

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.lingyun.lib.component.plugin.PluginContext
import com.lingyun.lib.component.plugin.PluginManager
import com.lingyun.lib.im.IMConfig
import com.lingyun.lib.im.api.IMPlugin
import com.lingyun.lib.im.api.IMSocketService
import com.lingyun.lib.network.api.NetworkPlugin
import com.lingyun.lib.network.api.OauthService
import com.lingyun.lib.user.api.UserPlugin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.DebugTree


/*
* Created by mc_luo on 5/2/21 7:23 PM.
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
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(DebugTree())
        }

//        initStrictMode()

        registerPlugin()
    }

    private fun initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build()
            )
            StrictMode.setVmPolicy(VmPolicy.Builder().detectAll().penaltyLog().build())
        }
    }

    private fun registerPlugin() {
        val baseUrl = "https://www.bluespace-x.com/"
        val dom = "bluespace-x"
        val clientId = "bluespace-x"
        val authProvider = "www.bluespace-x.com"
        val clientSecret = "bblusc112358"

        val pluginContext = PluginContext()
        pluginContext.setValue(Application::class.java.simpleName, this)
        pluginContext.setValue("debug", BuildConfig.DEBUG)
        pluginContext.setValue("baseUrl", baseUrl)
        pluginContext.setValue("dom", dom)
        pluginContext.setValue("authProvider", authProvider)
        pluginContext.setValue("clientId", clientId)
        pluginContext.setValue("clientSecret", "bblusc112358")


        IMConfig.dom = "bluespace-x"
//        IMConfig.serverUrl = "192.168.1.181"
        IMConfig.serverUrl = "119.28.141.187"
        IMConfig.serverPort = 50052
        IMConfig.applicationName = BuildConfig.APPLICATION_ID
        IMConfig.applicationVersion = BuildConfig.VERSION_NAME

        val networkPlugin = NetworkPlugin(baseUrl, dom, clientId, clientSecret)
        val imSocketPlugin = IMPlugin()
        val userPlugin = UserPlugin()


        PluginManager.registerPlugin(networkPlugin)
        PluginManager.registerPlugin(userPlugin)
        PluginManager.registerPlugin(imSocketPlugin)

        PluginManager.dependenciesOn(userPlugin, networkPlugin)
        PluginManager.dependenciesOn(imSocketPlugin, userPlugin)

        PluginManager.checkCloseCircle()
        PluginManager.optimizationHierarchy()
        Timber.e(PluginManager.printNode())
        PluginManager.startPlugin(pluginContext)

//        GlobalScope.launch {
//            val oauthService = networkPlugin.getServiceAsync(OauthService::class.java).await()
//
//            val tokenInfo = oauthService.awaitOauthTokenAvail()
//            IMConfig.userToken = tokenInfo.accessToken
//
//            val socketService = imSocketPlugin.getServiceAsync(IMSocketService::class.java).await()
//            socketService.startServiceAsync().await()
//        }

        GlobalScope.launch {
            val oauthService = networkPlugin.getServiceAsync(OauthService::class.java).await()
            val socketService = imSocketPlugin.getServiceAsync(IMSocketService::class.java).await()

            oauthService.subscribeOauthTokenInfo().collect {
                IMConfig.userToken = it.accessToken
                if (!it.isAccessTokenAvail()) {
                    socketService.stopServiceAsync().await()
                } else {
                    socketService.startServiceAsync()
                }
            }

        }
    }
}