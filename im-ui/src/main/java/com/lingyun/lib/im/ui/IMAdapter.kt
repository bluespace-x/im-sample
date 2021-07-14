package com.lingyun.lib.im.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalArgumentException
import java.sql.Timestamp

/*
* Created by mc_luo on 5/17/21 9:55 AM.
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
class IMAdapter(val messages: List<IMessage>, val inflater: LayoutInflater) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MessageType.SEND_TEXT.ordinal -> {
                val view = inflater.inflate(R.layout.item_send_text, parent, false)
                SendTextMessageViewHolder(view)
            }
            MessageType.RECEIVE_TEXT.ordinal -> {
                val view = inflater.inflate(R.layout.item_receive_txt, parent, false)
                ReceiveTextMessageViewHolder(view)
            }
            else -> {
                throw IllegalArgumentException("Unknow ViewType:$viewType")
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder is SendTextMessageViewHolder -> {
                holder.bindData(messages[position] as IMessage.TextMessage)
            }
            holder is ReceiveTextMessageViewHolder -> {
                holder.bindData(messages[position] as IMessage.TextMessage)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return messages[position].messageType.ordinal
    }

    class SendTextMessageViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        val messageView = lazy { root.findViewById<TextView>(R.id.aurora_tv_msgitem_message) }
        val displayName =
                lazy { root.findViewById<TextView>(R.id.aurora_tv_msgitem_sender_display_name) }
        val avatar = lazy { root.findViewById<ImageView>(R.id.aurora_iv_msgitem_avatar) }
        val data = lazy { root.findViewById<TextView>(R.id.aurora_tv_msgitem_date) }

        val progress = lazy { root.findViewById<ProgressBar>(R.id.aurora_pb_msgitem_sending) }
        val resend = lazy { root.findViewById<ImageButton>(R.id.aurora_ib_msgitem_resend) }
        val readed = lazy { root.findViewById<TextView>(R.id.aurora_tv_msgitem_read) }


        fun bindData(message: IMessage.TextMessage) {
            messageView.value.setText(message.message)
            displayName.value.setText(message.user.userName)
            avatar.value.setImageResource(R.drawable.aurora_headicon_default)
            data.value.setText(Timestamp(message.timestamp).toString())

            when (message.messageState) {
                MessageState.SENDING -> {
                    progress.value.visibility = View.VISIBLE
                    resend.value.visibility = View.GONE
                    readed.value.visibility = View.GONE

                }
                MessageState.SEND_FAIL -> {
                    progress.value.visibility = View.GONE
                    resend.value.visibility = View.VISIBLE
                    readed.value.visibility = View.GONE

                }
                MessageState.SEND_SUCCESS -> {
                    progress.value.visibility = View.GONE
                    resend.value.visibility = View.GONE
                    readed.value.visibility = View.GONE

                }
                MessageState.RECEIVERED -> {
                    progress.value.visibility = View.GONE
                    resend.value.visibility = View.GONE
                    readed.value.visibility = View.GONE

                }
                MessageState.READED -> {
                    progress.value.visibility = View.GONE
                    resend.value.visibility = View.GONE
                    readed.value.visibility = View.VISIBLE
                    
                }
                MessageState.REVOCATION -> {
                    progress.value.visibility = View.GONE
                    resend.value.visibility = View.GONE
                    readed.value.visibility = View.GONE

                }
            }
        }
    }

    class ReceiveTextMessageViewHolder(root: View) : RecyclerView.ViewHolder(root) {

        val messageView = lazy { root.findViewById<TextView>(R.id.aurora_tv_msgitem_message) }
        val displayName =
                lazy { root.findViewById<TextView>(R.id.aurora_tv_msgitem_receiver_display_name) }
        val avatar = lazy { root.findViewById<ImageView>(R.id.aurora_iv_msgitem_avatar) }
        val data = lazy { root.findViewById<TextView>(R.id.aurora_tv_msgitem_date) }

        fun bindData(message: IMessage.TextMessage) {
            messageView.value.setText(message.message)
            displayName.value.setText(message.user.userName)
            avatar.value.setImageResource(R.drawable.aurora_headicon_default)
            data.value.setText(Timestamp(message.timestamp).toString())
        }
    }


}