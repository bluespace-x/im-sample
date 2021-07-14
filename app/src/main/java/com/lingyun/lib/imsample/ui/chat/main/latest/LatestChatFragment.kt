package com.lingyun.lib.imsample.ui.chat.main.latest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lingyun.lib.imsample.R
import kotlinx.coroutines.launch

/*
* Created by mc_luo on 7/13/21 10:10 AM.
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
class LatestChatFragment : Fragment() {

    lateinit var adapter: LatestChatAdapter
    val latestMsgs = ArrayList<LatestMessage>()

    val chatViewModel: LatestChatViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_latest_chat, container, false)

        val msgList = root.findViewById<RecyclerView>(R.id.rv_msg_list)

        adapter = LatestChatAdapter(latestMsgs, inflater)
        val layoutManager = LinearLayoutManager(context)
        msgList.layoutManager = layoutManager
        msgList.adapter = adapter

        lifecycleScope.launch {
            val latestMessage = chatViewModel.loadGroupLatestMsg()

            latestMsgs.clear()
            latestMsgs.addAll(latestMessage)

        }
        return root
    }
}
