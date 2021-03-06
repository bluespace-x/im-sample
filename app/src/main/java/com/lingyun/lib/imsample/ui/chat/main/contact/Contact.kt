package com.lingyun.lib.imsample.ui.chat.main.contact

/*
* Created by mc_luo on 7/14/21 3:13 PM.
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
sealed class Contact(open val id: Long, open val name: String, open val avatar: String?)

class UserContact(override val id: Long, override val name: String, override val avatar: String?) : Contact(id, name, avatar)
class GroupContact(override val id: Long, override val name: String, override val avatar: String?) : Contact(id, name, avatar)