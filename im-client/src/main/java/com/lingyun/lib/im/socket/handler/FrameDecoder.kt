//package com.bluespace.core.protocol.server.handler
//
//import com.bluespace.position.positionservice.io.ProtocolConfig
//import com.bluespace.position.positionservice.io.handler.adapter.PacketHead
//import com.bluespace.position.positionservice.io.handler.adapter.PacketHeadConfig
//import com.bluespace.position.positionservice.io.handler.adapter.TailVerify
//import io.netty.buffer.ByteBuf
//import io.netty.channel.ChannelHandlerContext
//import io.netty.handler.codec.ByteToMessageDecoder
//
//
///**
// *     Before DECODE (2+2+n+4 bytes)                          After DECODE( n bytes )
// * +----------+----------+----------------+---------+         +----------------+
// * |  Header  | length   | Actual Content |  CRC    | ----->  | Actual Content |
// * | 0xCAFE   | 2+2+n+4  | "HELLO, WORLD" |  crc32  |         | "HELLO, WORLD" |
// * +----------+----------+----------------+---------+         +----------------+
// *
// */
//class FrameDecoder(val packetHeadConfig: PacketHeadConfig, val tailVerify: TailVerify,
//                   val minMsgLen: Int, val maxMsgLen: Int) : ByteToMessageDecoder() {
//
//
//    private var lastHalfPacketHead: PacketHead? = null
//    override fun decode(ctx: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
//
//        val packetHead = if (lastHalfPacketHead != null) {
//            lastHalfPacketHead
//        } else {
//            PacketHead.findNextHead(byteBuf, packetHeadConfig)
//        }
//
//        if (packetHead != null) {
//            val bytesOfMsg = packetHead.bytesOfMsg
//            val bytesOfHeadTag = packetHeadConfig.headTag.size
//
//            val readIndex = byteBuf.readerIndex()
//
//            //skip if msg len is too larger or too smaller
//            if (bytesOfMsg < minMsgLen || bytesOfMsg > maxMsgLen) {
//                lastHalfPacketHead = null
//                //skip readerIndex head tag
//                byteBuf.readerIndex(readIndex + bytesOfHeadTag)
//                return
//            }
//
//            val bytesOfTail = tailVerify.bytesOfTail()
//            //bytesOfHead = bytesOfHeadTag + bytesOfMsgLen
//            val bytesOfHead = packetHead.bytesOfHead
//            val bodyReadIndex = readIndex + bytesOfHead
//            val bytesOfBody = bytesOfMsg - bytesOfHead - bytesOfTail
//
//            if (byteBuf.readableBytes() >= bytesOfMsg) {
//                if (tailVerify.checkTail(byteBuf, readIndex, bytesOfMsg, ProtocolConfig.endianType)) {
//                    val bodyBuf = byteBuf.retainedSlice(bodyReadIndex, bytesOfBody)
//                    out.add(bodyBuf)
//                    byteBuf.readerIndex(readIndex + bytesOfMsg)
//                } else {
//                    byteBuf.readerIndex(readIndex + bytesOfHeadTag)
//                }
//                lastHalfPacketHead = null
//            } else {
//                lastHalfPacketHead = packetHead
//            }
//        } else {
//            lastHalfPacketHead = null
//        }
//
//    }
//
//
//}
//
