package com.lingyun.lib.network.api

import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/*
* Created by mc_luo on 5/7/21 6:17 PM.
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
const val ACCESS_TOKEN_MASK = 0x01
const val REFRESH_TOKEN_MASK = 0x02
const val REFRESHING_MASK = 0x04

@Serializable
data class OauthTokenInfo(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Float,
    private @Volatile var tokenState: Int = 0,
    @Transient
    val refreshTokenDeferred: CompletableDeferred<AuthorizationResult> =
        CompletableDeferred<AuthorizationResult>()
) {



    fun isAccessTokenAvail() = tokenState and ACCESS_TOKEN_MASK == 0

    fun isRefreshTokenAvail() = tokenState and REFRESH_TOKEN_MASK == 0

    fun isRefreshingToken() = tokenState and REFRESHING_MASK != 0


    fun tokenExpire() {
        tokenState = tokenState or ACCESS_TOKEN_MASK
    }

    fun refreshTokenExpire() {
        tokenState = tokenState or REFRESH_TOKEN_MASK
    }

    fun refreshToken() {
        tokenState = tokenState or REFRESHING_MASK
    }

    fun refreshTokenResult(result: AuthorizationResult) {
        refreshTokenDeferred.complete(result)

        when (result) {
            is AuthorizationResult.Authorization -> {

            }
            is AuthorizationResult.Unauthorization -> {
                refreshTokenExpire()
            }
        }
    }

    fun refreshTokenException(e: Throwable) {
        refreshTokenExpire()
        refreshTokenDeferred.completeExceptionally(e)
    }

    fun getBearerAccessToken(): String {
        return "Bearer $accessToken"
    }
}
