package com.lingyun.lib.imsample.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener
import cn.jiguang.imui.chatinput.model.FileItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.lingyun.lib.im.ui.*
import com.lingyun.lib.imsample.R
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.content_chat.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import proto.message.GroupId
import timber.log.Timber


class ChatActivity : AppCompatActivity() {

    lateinit var chatGroupId: GroupId
    private lateinit var mAdapter: IMAdapter
    private val imList = ArrayList<IMessage>()

    val chatViewModel: IMMessageViewModel by viewModels<IMMessageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        setContentView(R.layout.activity_chat)
        setSupportActionBar(findViewById(R.id.toolbar))
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        toolbar.title = title
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val ChatGroupIdData = intent.getByteArrayExtra("ChatGroupId")
        chatGroupId = GroupId.getDefaultInstance().parserForType.parseFrom(ChatGroupIdData)


        initMessageList()


        chat_input.setMenuClickListener(object : OnMenuClickListener {
            override fun onSendTextMessage(input: CharSequence?): Boolean {
                if (input.isNullOrEmpty()) {
                    return false
                }
                sendChatMessage(input.toString())
                return true
            }

            override fun onSendFiles(list: MutableList<FileItem>?) {

            }

            override fun switchToMicrophoneMode(): Boolean {
                return true
            }

            override fun switchToGalleryMode(): Boolean {
                return true
            }

            override fun switchToCameraMode(): Boolean {
                return true
            }

            override fun switchToEmojiMode(): Boolean {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendChatMessage(text: String) {
        val user = DefaultUser("14", "疾风", "R.drawable.ic_launcher")
        val message = IMessage.TextMessage(
                "1",
                MessageType.SEND_TEXT,
                System.currentTimeMillis(),
                user,
                message = text,
                MessageState.SENDING
        )

        imList.add(message)
        mAdapter.notifyDataSetChanged()

        lifecycleScope.launch {
            try {
                chatViewModel.sendMessageAsync(message, chatGroupId).await()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

    }

    private fun initMessageList() {
        val inflater = getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mAdapter = IMAdapter(imList, inflater)

        val layoutManager = LinearLayoutManager(this)
        msg_list.layoutManager = layoutManager
        msg_list.adapter = mAdapter

        msg_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                val lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

                if (firstCompletelyVisibleItemPosition != NO_POSITION) {

                    for (i in firstCompletelyVisibleItemPosition..lastCompletelyVisibleItemPosition) {
                        val msg = imList[i]
                        if (msg.isReceiveMessage()) {
                            if (msg.messageState == MessageState.RECEIVERED) {
                                msg.messageState = MessageState.READED
                                chatViewModel.onMessageReaded(msg)
                            }
                        }
                    }
                }
            }
        })

        lifecycleScope.launch {
            chatViewModel.subscribeMessage(chatGroupId).collect {
                imList.add(it)
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object {

        fun launcher(context: Context, groupId: GroupId) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("ChatGroupId", groupId.toByteArray())
            context.startActivity(intent)
        }
    }
}