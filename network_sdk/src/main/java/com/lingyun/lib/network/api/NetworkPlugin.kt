package com.lingyun.lib.network.api

import android.app.Application
import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lingyun.lib.component.plugin.AbstractPlugin
import com.lingyun.lib.component.plugin.PluginContext
import com.lingyun.lib.network.OauthTokenManager
import com.lingyun.lib.network.interceptor.HeadInterceptor
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/*
* Created by mc_luo on 5/12/21 5:07 PM.
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
class NetworkPlugin(val oauthBaseUrl: String, val dom: String, val clientId: String, val clientSecret: String)
    : AbstractPlugin(CoroutineScope(Dispatchers.IO + SupervisorJob()), NetworkPlugin::class.java.simpleName) {

    lateinit var retrofit: Retrofit
    override suspend fun config(context: PluginContext) {
        Log.e("NetworkPlugin", "config")
        val app = context.getValue<Application>(Application::class.java.simpleName)
        val debug = context.getValue<Boolean>("debug")
        val dom = context.getValue<String>("dom")
        val authProvider = context.getValue<String>("authProvider")

        val headParams = HashMap<String, String>().also {
            it["Dom"] = dom
            it["Client-Id"] = clientId
        }

        OauthTokenManager.clientId = clientId
        OauthTokenManager.clientSecret = clientSecret

        val headIntercept = HeadInterceptor(headParams)
//        val contentType = "application/json; charset=utf-8".toMediaType()
//        val converterFactory = Json.asConverterFactory(contentType)
        retrofit = RetrofitFactory.createRetrofit(app, dom,authProvider,oauthBaseUrl, debug = debug,headInterceptor = headIntercept)
        registerService(OauthService::class.java, OauthService(retrofit))

        OauthTokenManager.init(app)

    }

    override fun executeAsync(context: PluginContext): Deferred<Any> {
        Log.e("NetworkPlugin", "executeAsync")
        return CompletableDeferred(true)
    }
}