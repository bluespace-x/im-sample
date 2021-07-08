package com.lingyun.lib.network.api

import com.google.gson.Gson
import com.lingyun.lib.network.OauthTokenManager
import com.lingyun.lib.network.oauth.IOauthTokenService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Retrofit

/*
* Created by mc_luo on 5/12/21 5:32 PM.
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
class OauthService(val retrofit: Retrofit) {


    fun oauthTokenAsync(
        userName: String,
        password: String,
        scope: String = "all"
    ): Deferred<AuthorizationResult> {
        return OauthTokenManager.oauthTokenAsync(userName, password, scope)
    }

    internal fun oauthTokenRemoteAsync(
        basicAuthenticator: String,
        userName: String,
        password: String,
        scope: String = "all"
    ): Deferred<AuthorizationResult> {
        return GlobalScope.async {
            val result = retrofit.create(IOauthTokenService::class.java)
                .oauthTokenAsync(basicAuthenticator, userName, password, scope).await()
            val resultStr = Gson().toJson(result)

            return@async jsonToAuthorizationResult(resultStr)
        }
    }

    internal fun refreshTokenRemoteAsync(
        basicAuthenticator: String,
        refreshToken: String
    ): Deferred<AuthorizationResult> {
        return GlobalScope.async {
            val result = retrofit.create(IOauthTokenService::class.java)
                .refreshTokenAsync(basicAuthenticator, refreshToken).await()
            val resultStr = Gson().toJson(result)
            return@async jsonToAuthorizationResult(resultStr)
        }
    }

    private fun jsonToAuthorizationResult(result: String): AuthorizationResult {
        try {
            val unauthorization =
                Json.decodeFromString(AuthorizationResult.Unauthorization.serializer(), result)
            return unauthorization
        } catch (e: Exception) {

        }
        val authorization =
            Json.decodeFromString(AuthorizationResult.Authorization.serializer(), result)
        return authorization
    }

    fun historyOauthTokenInfo(): OauthTokenInfo? {
        return OauthTokenManager.getOauthTokenInfo()
    }

    suspend fun awaitOauthTokenAvail(): OauthTokenInfo {
        return OauthTokenManager.subscribeOauthToken().first { it.isAccessTokenAvail() }
    }

    fun subscribeOauthTokenInfo(): Flow<OauthTokenInfo> {
        return OauthTokenManager.subscribeOauthToken()
    }
}