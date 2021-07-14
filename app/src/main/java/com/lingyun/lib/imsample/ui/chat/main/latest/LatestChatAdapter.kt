package com.lingyun.lib.imsample.ui.chat.main.latest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lingyun.lib.im.dao.model.Message
import com.lingyun.lib.imsample.R

/*
* Created by mc_luo on 7/13/21 10:34 AM.
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
class LatestChatAdapter(val messages: List<LatestMessage>, val inflater: LayoutInflater) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val root = inflater.inflate(R.layout.item_latest_chat, parent, false)
        val viewHolder = LatestChatViewHolder(root)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as LatestChatViewHolder).bindData(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class LatestChatViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        fun bindData(message: LatestMessage) {

        }
    }
}