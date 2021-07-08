package com.lingyun.lib.network.api

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lingyun.lib.network.TokenAuthenticator
import com.lingyun.lib.network.interceptor.HeadInterceptor
import com.lingyun.lib.network.interceptor.HttpLoggingInterceptor
import com.lingyun.lib.network.interceptor.TokenInterceptor
import com.readystatesoftware.chuck.ChuckInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.AuthProvider
import java.util.*
import java.util.concurrent.TimeUnit


/*
* Created by mc_luo on 2020/10/9.
* Copyright (c) 2020 The LingYun Authors. All rights reserved.
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
object RetrofitFactory {
    private const val CONNECT_TIMEOUT: Long = 5 * 1000
    private const val READ_TIMEOUT: Long = 10 * 1000
    private const val WRITE_TIMEOUT: Long = 10 * 1000

    private val contentType = "application/json; charset=utf-8".toMediaType()
//    private val mConverterFactory = Json.asConverterFactory(contentType)
    private val mConverterFactory = GsonConverterFactory.create()

    fun createRetrofit(
        context: Context,
        dom:String,
        authProvider: String,
        baseUrl: String,
        debug: Boolean = true,
        headInterceptor: HeadInterceptor? = null,
        converterFactory: Converter.Factory = mConverterFactory,
        connectTimeout: Long = CONNECT_TIMEOUT,
        readTimeout: Long = READ_TIMEOUT, writeTimeout: Long = WRITE_TIMEOUT
    ): Retrofit {
        //first step init okhttp client
        val protocols = ArrayList<Protocol>()
        protocols.add(Protocol.HTTP_1_1)
        protocols.add(Protocol.HTTP_2)
        val builder = OkHttpClient.Builder()
            .protocols(protocols)
            // .retryOnConnectionFailure(true)
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)


        headInterceptor?.let {
            builder.addInterceptor(headInterceptor)
        }

        builder.addInterceptor(TokenInterceptor(dom,authProvider))

        builder.authenticator(TokenAuthenticator())

        builder.addInterceptor(HttpLoggingInterceptor().apply {
            if (debug) {
                level = HttpLoggingInterceptor.Level.BODY
            } else {
                level = HttpLoggingInterceptor.Level.NONE
            }
        })

        if (debug) {
            builder.addInterceptor(ChuckInterceptor(context))
        }

//        val contentType = "application/json; charset=utf-8".toMediaType()
        val client = builder.build()
        return Retrofit.Builder()
            .client(client)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(baseUrl)
            .build()
    }

}