package com.lingyun.lib.network

import com.lingyun.lib.network.api.AuthorizationResult
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.*
import timber.log.Timber

class TokenAuthenticator() : Authenticator {
    private val TAG = "TokenAuthenticator"


    override fun authenticate(route: Route?, response: okhttp3.Response): Request? = runBlocking {
        val originAuthorization = response.request.headers["Authorization"]
        val path = response.request.url.toUrl().path
        val grantType = response.request.header("grant_type")

        Timber.e("authenticate path:$path grantType:$grantType")
        if (path == "/oauth/token") {
            return@runBlocking null
        }

        try {
            val authorizationResult = withTimeoutOrNull(6000) {
                OauthTokenManager.tryRefreshTokenAsync(originAuthorization).await()
            }
            if (authorizationResult is AuthorizationResult.Authorization) {
                return@runBlocking response.request.newBuilder()
                    .header("Authorization", OauthTokenManager.getBearerAccessToken()!!)
                    .build()
            }
        } catch (e: Exception) {
            return@runBlocking null
        }

        return@runBlocking null
    }
}
