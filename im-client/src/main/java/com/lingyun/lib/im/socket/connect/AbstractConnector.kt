package com.lingyun.lib.im.socket.connect

import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.atomic.AtomicReference

/*
* Created by mc_luo on 2020/10/19 1:52 PM.
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
abstract class AbstractConnector<T : Any, V : Any> : IConnector<T, V> {

    private val connectState = AtomicReference(ConnectState.IDLE)
    private val connectStateChannel =
            MutableSharedFlow<Boolean>(replay = 1, extraBufferCapacity = 16)
    private val disconnectFuture = CompletableDeferred<V>()
    private val connectFuture = CompletableDeferred<T>()
    private var connectException: Throwable? = null
    private var disconnectException: Throwable? = null

    private var connectListener: ConnectListener<T>? = null

    override fun connectState(): AtomicReference<ConnectState> {
        return connectState
    }

    override fun setConnectListener(connectListener: ConnectListener<T>?) {
        this.connectListener = connectListener
    }

    fun onConnecting(obj: T) {
        connectState.set(ConnectState.CONNECTING)
        connectListener?.onConnecting(obj)
    }

    fun onConnectCancel(e: Throwable) {
        connectException = e
        connectFuture.completeExceptionally(e)
    }

    fun onConnectComplete(e: Throwable?, obj: T?) {
        connectException = e
        val state = if (e != null) ConnectState.CONNECT_FAIL else ConnectState.CONNECT_SUCCESS
        connectState.set(state)

        if (e == null) {
            connectFuture.complete(obj!!)
            connectStateChannel.tryEmit(true)
        } else {
            connectFuture.completeExceptionally(e)
        }

        connectListener?.onConnectComplete(obj!!, e)
    }

    fun onDisconnecting(obj: T) {
        connectState.set(ConnectState.DISCONNECTING)
        connectListener?.onDisconnecting(obj)
    }

    fun onDisconnected(e: Throwable?, obj1: T, obj2: V?) {
        connectState.set(ConnectState.DISCONNECTED)
        disconnectException = e
        if (e != null) {
            disconnectFuture.completeExceptionally(e)
        } else {
            disconnectFuture.complete(obj2!!)
            connectStateChannel.tryEmit(false)
        }
        connectListener?.onDisconnected(obj1, null)
    }

    override fun isIdle(): Boolean {
        return connectState.get() == ConnectState.IDLE
    }

    override fun isConnecting(): Boolean {
        return connectState.get() == ConnectState.CONNECTING
    }

    override fun isConnecteComplete(): Boolean {
        return connectState.get() == ConnectState.CONNECT_SUCCESS || connectState.get() == ConnectState.CONNECT_FAIL
    }

    override fun connectException(): Throwable? {
        return connectException
    }

    override fun isDisconnecting(): Boolean {
        return connectState.get() == ConnectState.DISCONNECTING
    }

    override fun isDisconnected(): Boolean {
        return connectState.get() == ConnectState.DISCONNECTED
    }

    override fun disconnectException(): Throwable? {
        return disconnectException
    }

    override fun subscribeConnectState() = liveData {
        emitSource(connectStateChannel.asLiveData())
    }

    override fun connectFuture(): CompletableDeferred<T> {
        return connectFuture
    }

    override fun disconnectFuture(): CompletableDeferred<V> {
        return disconnectFuture
    }
}