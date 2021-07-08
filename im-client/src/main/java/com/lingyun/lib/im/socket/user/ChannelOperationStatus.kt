package com.lingyun.lib.im.socket.user

/*
* Created by mc_luo on 6/8/21 3:57 PM.
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
enum class ChannelOperationStatus(val value: Int, val msg: String) {
    EXCHANGE_PROTOCOL(0, "exchange protocol"),
    TOKEN_VERIFY(1, "token verify"),
    INIT(2, "init"),
    DATA_READY(3, "data ready"),
    DISCONNECTING(4, "disconnecting"),
    DISCONNECTED(5, "disconnected"),
}

fun buildChannelOperationStatusByValue(value: Int): ChannelOperationStatus {
    return when (value) {
        0 -> ChannelOperationStatus.EXCHANGE_PROTOCOL
        1 -> ChannelOperationStatus.TOKEN_VERIFY
        2 -> ChannelOperationStatus.INIT
        3 -> ChannelOperationStatus.DATA_READY
        4 -> ChannelOperationStatus.DISCONNECTING
        5 -> ChannelOperationStatus.DISCONNECTED
        else -> throw UnknownError("unknow operation status:$value")
    }
}