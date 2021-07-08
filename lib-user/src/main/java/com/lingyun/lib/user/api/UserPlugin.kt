package com.lingyun.lib.user.api

import android.app.Application
import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lingyun.lib.component.plugin.AbstractPlugin
import com.lingyun.lib.component.plugin.PluginContext
import com.lingyun.lib.network.api.RetrofitFactory
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.EmptySerializersModule
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/*
* Created by mc_luo on 5/19/21 1:49 PM.
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
class UserPlugin :
    AbstractPlugin(
        CoroutineScope(Dispatchers.IO + SupervisorJob()),
        UserPlugin::class.java.simpleName
    ) {

    private lateinit var retrofit: Retrofit

    override suspend fun config(context: PluginContext) {
        Log.e("UserPlugin", "config")
        val app = context.getValue<Application>(Application::class.java.simpleName)
        val debug = context.getValue<Boolean>("debug")
        val baseUrl = context.getValue<String>("baseUrl")
        val dom = context.getValue<String>("dom")
        val authProvider = context.getValue<String>("authProvider")

        val contentType = "application/json; charset=utf-8".toMediaType()

        val converterFactory =
            Json { isLenient = true;ignoreUnknownKeys = true }.asConverterFactory(contentType)

        retrofit = RetrofitFactory.createRetrofit(
            app, dom, authProvider, baseUrl, debug = debug, converterFactory = converterFactory
        )

        val userService = UserService(this, retrofit)
        registerService(UserService::class.java, userService)
    }

    override fun executeAsync(context: PluginContext): Deferred<Any> {
        Log.e("UserPlugin", "executeAsync")
        return CompletableDeferred(true)
    }
}