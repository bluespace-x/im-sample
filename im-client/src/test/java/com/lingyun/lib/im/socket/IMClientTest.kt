package com.lingyun.lib.im.socket

import com.lingyun.lib.im.IMConfig
import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.im.api.IMSocketService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

/*
 * Created by mc_luo on 5/13/21 5:19 PM.
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
class IMClientTest {

    @Test
    fun testConnect() {
        val serverUrl = "localhost"
        val port = 50052

        IMConfig.applicationName = "app/im-client"
        IMConfig.applicationVersion = "0.0.1"
        IMConfig.dom = "bluespace-x"
        IMConfig.userToken = "1312fasdas"
        IMConfig.tokenProvider = "bluespace-x"
        IMConfig.serverUrl = "localhost"
        IMConfig.serverPort = 50052

        val imService = IMSocketService()

        runBlocking {
            val success = imService.startServiceAsync().await()
            assertEquals(true, success)

            val chat = Message().also {
                it.message = "hello im"
            }
            imService.sendIMMessageAsync(chat).await()
        }
    }
}