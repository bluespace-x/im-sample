package com.lingyun.lib.network

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.lingyun.lib.component.plugin.PluginManager
import com.lingyun.lib.network.api.AuthorizationResult
import com.lingyun.lib.network.api.NetworkPlugin
import com.lingyun.lib.network.api.OauthService
import com.lingyun.lib.network.api.OauthTokenInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import java.util.concurrent.atomic.AtomicInteger

/*
* Created by mc_luo on 5/7/21 6:06 PM.
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
internal object OauthTokenManager {
    const val TOKEN_IDLE = 0
    const val OAUTH_TOKENING = 1
    const val REFRESH_TOKENING = 2

    var clientId: String = ""
    var clientSecret: String = ""

    val oauthServiceFuture = GlobalScope.async {
        PluginManager.getPlugin(NetworkPlugin::class.java.simpleName)!!
            .getServiceAsync(OauthService::class.java).await()
    }

    private val tokenInfoFlow =
        MutableSharedFlow<OauthTokenInfo>(replay = 1, extraBufferCapacity = 16)

    private lateinit var application: Application

    @Volatile
    private var lastTokenInfo: OauthTokenInfo? = null
        set(value) {
            field = value
            if (value != null) {
                tokenInfoFlow.tryEmit(value)
            }
        }

    private var state = AtomicInteger(TOKEN_IDLE)

    private var refreshTokenJob: Job? = null
    private var oauthTokenDeferred: Deferred<AuthorizationResult>? = null


    fun init(application: Application) {
        OauthTokenManager.application = application

        val sp = PreferenceManager.getDefaultSharedPreferences(application)
        val tokenInfoStr = sp.getString("token_info", null)
        if (tokenInfoStr != null) {
            val info = Json.decodeFromString(OauthTokenInfo.serializer(), tokenInfoStr)
            updateTokenInfo(info)
        }
    }

    fun updateTokenInfo(tokenInfo: OauthTokenInfo) {
        Log.e("OauthTokenManager", "updateTokenInfo:$tokenInfo")
        lastTokenInfo = tokenInfo
        val sp = PreferenceManager.getDefaultSharedPreferences(application)
        sp.edit()
            .putString("token_info", Json.encodeToString(OauthTokenInfo.serializer(), tokenInfo))
            .apply()
    }

    fun clientAuthorize(): String {
        return Credentials.basic(clientId, clientSecret)
    }

    fun tryRefreshTokenAsync(originAuthorization: String?): Deferred<AuthorizationResult> {
        return when (originAuthorization) {
            null -> CompletableDeferred(
                AuthorizationResult.Unauthorization(
                    "unauthorization",
                    "already unauthorization"
                )
            )
            getBearerAccessToken() -> {
                refreshTokenAsync()
            }
            else -> {
                when (state.get()) {
                    REFRESH_TOKENING, OAUTH_TOKENING -> {
                        if (oauthTokenDeferred != null) oauthTokenDeferred!! else CompletableDeferred(
                            AuthorizationResult.Unauthorization(
                                "unauthorization",
                                "already unauthorization"
                            )
                        )
                    }
                    else -> CompletableDeferred(
                        AuthorizationResult.Unauthorization(
                            "unauthorization",
                            "already unauthorization"
                        )
                    )
                }
            }
        }
    }

    fun refreshTokenAsync(): Deferred<AuthorizationResult> {
        val tokenInfo = lastTokenInfo
        when (state.get()) {
            TOKEN_IDLE -> {
                if (tokenInfo == null || !tokenInfo.isRefreshTokenAvail()) {
                    return CompletableDeferred(
                        AuthorizationResult.Unauthorization(
                            "unauthorization",
                            "already unauthorization"
                        )
                    )
                }

                if (tokenInfo.isRefreshingToken()) return tokenInfo.refreshTokenDeferred

                tokenInfo.refreshToken()
                val job = Job()
                refreshTokenJob = job

                state.set(REFRESH_TOKENING)
                val deferred = GlobalScope.async(job + Dispatchers.IO) {
                    try {
                        val authentication = oauthServiceFuture.await()
                            .refreshTokenRemoteAsync(clientAuthorize(), tokenInfo.refreshToken)
                            .await()

                        when (authentication) {
                            is AuthorizationResult.Authorization -> {
                                val oauthTokenInfo = OauthTokenInfo(
                                    authentication.accessToken,
                                    authentication.refreshToken,
                                    authentication.expiresIn
                                )
                                updateTokenInfo(oauthTokenInfo)
                            }
                            is AuthorizationResult.Unauthorization -> {
                            }
                        }
                        tokenInfo.refreshTokenResult(authentication)
                        tokenInfoFlow.tryEmit(tokenInfo)

                        authentication
                    } catch (e: Throwable) {
                        tokenInfo.refreshTokenException(e)
                        throw e
                    } finally {
                        state.compareAndSet(REFRESH_TOKENING, TOKEN_IDLE)
                    }
                }
                oauthTokenDeferred = deferred

                return deferred
            }
            REFRESH_TOKENING, OAUTH_TOKENING -> {
                return oauthTokenDeferred!!
            }
            else -> throw java.lang.IllegalArgumentException("Unknow state:${state.get()}")
        }
    }

    fun oauthTokenAsync(
        userName: String,
        password: String,
        scope: String = "all"
    ): Deferred<AuthorizationResult> {
        return when (state.get()) {
            TOKEN_IDLE, REFRESH_TOKENING -> {
                refreshTokenJob?.cancel()
                state.set(OAUTH_TOKENING)

                val deferred = GlobalScope.async {
                    try {
                        val authorization = oauthServiceFuture.await()
                            .oauthTokenRemoteAsync(clientAuthorize(), userName, password, scope)
                            .await()
                        when (authorization) {
                            is AuthorizationResult.Authorization -> {
                                val oauthTokenInfo = OauthTokenInfo(
                                    authorization.accessToken,
                                    authorization.refreshToken,
                                    authorization.expiresIn
                                )
                                updateTokenInfo(oauthTokenInfo)
                            }
                            is AuthorizationResult.Unauthorization -> {

                            }
                        }
                        authorization
                    } finally {
                        state.set(TOKEN_IDLE)
                    }
                }
                oauthTokenDeferred = deferred
                deferred
            }
            OAUTH_TOKENING -> {
                return oauthTokenDeferred!!
            }
            else -> throw IllegalArgumentException("unknow state:${state.get()}")
        }
    }

    fun getBearerAccessToken(): String? {
        return lastTokenInfo?.getBearerAccessToken()
    }

    fun getOauthTokenInfo() = lastTokenInfo

    fun subscribeOauthToken() = tokenInfoFlow
}