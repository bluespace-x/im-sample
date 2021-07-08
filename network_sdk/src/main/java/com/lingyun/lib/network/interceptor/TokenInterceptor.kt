package com.lingyun.lib.network.interceptor

import android.util.Log
import com.lingyun.lib.network.OauthTokenManager
import com.lingyun.lib.network.OauthTokenWhiteList
import okhttp3.Interceptor
import okhttp3.Response

/*
* Created by mc_luo on 5/7/21 6:07 PM.
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
class TokenInterceptor(val dom: String, val authProvider: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (OauthTokenWhiteList.inOauthWhiteList(originalRequest.url.toUrl().path)) {
            return chain.proceed(originalRequest)
        }


        val bearAccessToken = OauthTokenManager.getBearerAccessToken()
        Log.e("TokenInterceptor", "bearAccessToken:$bearAccessToken")
        if (bearAccessToken == null) return chain.proceed(originalRequest)

        val compressedRequest = originalRequest.newBuilder()
        if (originalRequest.header("Authorization") == null) {
            compressedRequest.addHeader("Authorization", bearAccessToken)
            compressedRequest.addHeader("Dom", dom)
            compressedRequest.addHeader("Auth-Provider", authProvider)
        }
        return chain.proceed(compressedRequest.build())
    }
}