package com.lingyun.lib.im.socket

import com.lingyun.lib.im.exception.IMException
import com.lingyun.lib.im.extensions.asDeffer
import com.lingyun.lib.im.api.IMSocketService
import com.lingyun.lib.im.socket.connect.AbstractConnector
import com.lingyun.lib.im.socket.connect.ConnectState
import com.lingyun.lib.im.socket.handler.*
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.codec.protobuf.ProtobufDecoder
import io.netty.handler.codec.protobuf.ProtobufEncoder
import io.netty.handler.timeout.IdleStateHandler
import kotlinx.coroutines.*
import proto.message.Packet
import java.net.ConnectException

/*
* Created by mc_luo on 4/30/21 3:27 PM.
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
class IMClient(val imService: IMSocketService, val serverUrl: String, val serverPort: Int) :
    AbstractConnector<IMClient, Boolean>(),
    CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {

    private var bootstrap: Bootstrap? = null
    private var workerGroup: EventLoopGroup? = null

    private var channelFuture: ChannelFuture? = null

    private lateinit var socketChannel: SocketChannel


    override fun connectAsync(): Deferred<IMClient> {
        if (connectState().get() != ConnectState.IDLE) {
            throw ConnectException("bad connect by connector is wrong state: ${connectState().get()}")
        }

        onConnecting(this)

        var connectException: Throwable? = null
        launch {
            val future = if (Epoll.isAvailable()) {
                startEpollService(serverUrl, serverPort)
            } else {
                startNioService(serverUrl, serverPort)
            }
            channelFuture = future

            try {
                future.asDeffer().await()
                connectException = future.cause()
                if (future.isSuccess) {
                    onConnectComplete(null, this@IMClient)
                    //await future close
                    future.channel().closeFuture().asDeffer().await()
                } else {
                    onConnectComplete(future.cause(), this@IMClient)
                }
            } catch (e: Throwable) {
                connectException = e
                onDisconnected(e, this@IMClient, true)
            } finally {
                if (!isConnecteComplete()) {
                    val e = connectException ?: CancellationException()
                    onConnectCancel(e)
                }

                workerGroup?.shutdownGracefully()?.asDeffer()?.await()
                onDisconnected(connectException, this@IMClient, true)
            }
        }
        return connectFuture()
    }

    override fun disconnectAsync(): Deferred<Boolean> {
        return when {
            isConnecteComplete() || isConnecting() -> {
                onDisconnecting(this)

                channelFuture?.cancel(true)
                workerGroup?.shutdownGracefully()
                disconnectFuture()
            }
            isDisconnecting() || isDisconnected() -> {
                disconnectFuture()
            }
            else -> {
                CompletableDeferred(false)
            }
        }
    }


    private fun startEpollService(address: String, port: Int): ChannelFuture {
        val bootstrap = Bootstrap()
        val workerGroup = EpollEventLoopGroup()
        bootstrap.group(workerGroup)
        initOption(bootstrap)
        bootstrap.channel(EpollSocketChannel::class.java)
            .handler(object : ChannelInitializer<EpollSocketChannel>() {
                @Throws(Exception::class)
                override fun initChannel(ch: EpollSocketChannel) {
                    initSocketChannle(ch)
                }
            })
        this.bootstrap = bootstrap
        this.workerGroup = workerGroup
        return bootstrap.connect(address, port)
    }

    private fun startNioService(address: String, port: Int): ChannelFuture {
        val bootstrap = Bootstrap()
        val workerGroup = NioEventLoopGroup()

        bootstrap.group(workerGroup)
        initOption(bootstrap)
        bootstrap.channel(NioSocketChannel::class.java)
            .handler(object : ChannelInitializer<NioSocketChannel>() {
                @Throws(Exception::class)
                override fun initChannel(ch: NioSocketChannel) {

                    ch.config().also {
                        it.setKeepAlive(true)
                            .setTcpNoDelay(true)
                            .setWriteSpinCount(16)
                            .setWriteBufferWaterMark(WriteBufferWaterMark(32 * 1024, 64 * 1024))
                    }

                    initSocketChannle(ch)
                }
            })

        this.bootstrap = bootstrap
        this.workerGroup = workerGroup
        return bootstrap.connect(address, port)
    }

    private fun initOption(bootstrap: Bootstrap) {
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.SO_TIMEOUT, 6 * 1000)
            .option(ChannelOption.TCP_NODELAY, false)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15 * 1000)
//            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//            .option(ChannelOption.WRITE_SPIN_COUNT, 16)
//            .option(
//                ChannelOption.WRITE_BUFFER_WATER_MARK,
//                WriteBufferWaterMark(32 * 1024, 64 * 1024)
//            )
    }

    private fun initSocketChannle(ch: SocketChannel) {
        socketChannel = ch
        ch.pipeline().also {
            it.addLast("timeout", IdleStateHandler(60, 3, 60))
            it.addLast(
                "frameDecoder", LengthFieldBasedFrameDecoder(
                    1024, 0,
                    2, 0, 2, true
                )
            )
            it.addLast(
                "protobufDecoder",
                ProtobufDecoder(Packet.getDefaultInstance())
            )
            it.addLast("frameEncoder", LengthFieldPrepender(2))
            it.addLast("protobufEncoder", ProtobufEncoder())
            it.addLast("timeoutHandler", TimeOutHandler())

            it.addLast("messageDecoder", MessageDecoder())
            it.addLast("messageEndocer", MessageEncoder())
            it.addLast("ackHandler", AckHandler())
            it.addLast("responseHandler", ResponseHandler())
            it.addLast("userHandler", ClientHandler(imService))

        }
    }

    fun sendMessage(msg: Message) {
        if (!isConnecteComplete()) {
            throw IMException("Only connect complete can send packet")
        }

        socketChannel.writeAndFlush(msg)
    }
}