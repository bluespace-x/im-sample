package com.lingyun.lib.network.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/*
 * Created by mc_luo on 5/13/21 11:11 AM.
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
class AuthorizationResultSerializerTest {

    @get:Rule
    val server = MockWebServer()

    private lateinit var service: Service

    interface Service {
        @GET("/")
        fun deserialize(): Deferred<AuthorizationResult>

        @POST("/")
        fun serialize(@Body authorizationResult: AuthorizationResult): Deferred<Void?>
    }

    @Before
    fun setUp() = runBlocking {
        val module = SerializersModule {
            contextual(AuthorizationResultSerializer)
        }

        val contentType = "application/json; charset=utf-8".toMediaType()
        val converterFactory = Json { serializersModule = module }.asConverterFactory(contentType)

        val retrofit = Retrofit.Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(converterFactory)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()

        service = retrofit.create(Service::class.java)
    }

    @Test
    fun serialize() = runBlocking {
        val unauthorization = AuthorizationResult.Unauthorization(error = "error", errorDescription = "description")
        println(Json.encodeToString(AuthorizationResult.Unauthorization.serializer(),unauthorization))
        server.enqueue(MockResponse().setBody("""{"error_description":"description","error":"error"}"""))
        val result = service.deserialize().await()
        assertEquals(AuthorizationResult.Unauthorization(error = "error", errorDescription = "description"), result)
    }

    fun deserialize() {

    }
}