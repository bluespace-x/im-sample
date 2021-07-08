package com.lingyun.lib.network.oauth

import com.lingyun.lib.network.api.AuthorizationResult
import kotlinx.coroutines.Deferred
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

/*
* Created by mc_luo on 5/8/21 4:31 PM.
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
interface IOauthTokenService {

    @POST("/oauth/token")
    @FormUrlEncoded
    fun oauthTokenAsync(
        @Header("Authorization") basicAuthenticator: String,
        @Field("username") userName: String,
        @Field("password") password: String,
        @Field("scope") scope: String,
        @Field("grant_type") grantType: String = "password"
    ): Deferred<Any>

    @POST("/oauth/token")
    @FormUrlEncoded
    fun refreshTokenAsync(
        @Header("Authorization") basicAuthenticator: String,
        @Field("refresh_token") refershToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): Deferred<Any>
}