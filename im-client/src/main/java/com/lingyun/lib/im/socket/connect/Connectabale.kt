package com.lingyun.lib.im.socket.connect

import kotlinx.coroutines.Deferred

/*
* Created by mc_luo on 2020/10/19 1:51 PM.
* Copyright (c) 2020 The LingYun Authors. All rights reserved.
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
interface Connectabale<T> {
     fun connectAsync(): Deferred<T>
}