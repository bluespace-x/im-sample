package com.lingyun.lib.im.socket

/*
* Created by mc_luo on 2021/3/21 .
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
object SocketConfig {
    const val PORT = 50052

    val READ_TIMEOUT = 2 * 60 * 1000
    val WRITE_TIMEOUT = 2 * 60 * 1000

    val TOTAL_TIMEOUT = 5 * 60 * 1000

    val ACK_TIMEOUT = 3000L

    val ACK_RETRY_COUNT = 9
}