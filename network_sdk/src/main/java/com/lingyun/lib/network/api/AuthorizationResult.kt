package com.lingyun.lib.network.api

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/*
* Created by mc_luo on 5/12/21 10:35 AM.
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
@Serializable
sealed class AuthorizationResult {

    @Serializable
    data class Authorization(
            @SerialName("access_token") val accessToken: String,
            @SerialName("refresh_token") val refreshToken: String,
            @SerialName("expires_in") val expiresIn: Float,
            @SerialName("scope") val scope: String,
            @SerialName("token_type") val tokenType: String
    ) : AuthorizationResult()

    @Serializable
    data class Unauthorization(
        @SerialName("error") val error: String,
        @SerialName("error_description") val errorDescription: String
    ) : AuthorizationResult()

}

object AuthorizationResultSerializer : KSerializer<AuthorizationResult>{
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AuthorizationResult", PrimitiveKind.STRING)

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): AuthorizationResult {
        println("deserialize:${decoder}")
        try {
            val authorization =  decoder.decodeSerializableValue(AuthorizationResult.Authorization.serializer())
            return authorization
        } catch (e: Exception) {
            return decoder.decodeSerializableValue(AuthorizationResult.Unauthorization.serializer())
        }
    }

    override fun serialize(encoder: Encoder, value: AuthorizationResult) {
        when(value){
            is AuthorizationResult.Authorization->{
                encoder.encodeSerializableValue(AuthorizationResult.Authorization.serializer(),value)
            }
            is AuthorizationResult.Unauthorization->{
                encoder.encodeSerializableValue(AuthorizationResult.Unauthorization.serializer(),value)
            }
        }
    }

}