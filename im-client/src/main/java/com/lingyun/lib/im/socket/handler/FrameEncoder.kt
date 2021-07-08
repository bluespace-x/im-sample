//package com.bluespace.core.protocol.server.handler
//
//import com.bluespace.core.common.utils.CRC32
//import io.netty.buffer.ByteBuf
//import io.netty.channel.ChannelHandlerContext
//import io.netty.handler.codec.MessageToByteEncoder
//
///**
// *
// *  Before ENCODE (n bytes)        After ENCODE (2+2+n+4 bytes)
// * +----------------+      +----------+----------+----------------+---------+
// * | Actual Content |----->|  Header  | length   | Actual Content |  CRC    |
// * | "HELLO, WORLD" |      | 0xCAFE   | 2+2+n+4  | "HELLO, WORLD" |  crc32  |
// * +----------------+      +----------+----------+----------------+---------+
// *
// */
//class FrameEncoder : MessageToByteEncoder<ByteBuf>() {
//
//    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
//        val bodyLen = msg.readableBytes()
//        val messageLen = 8+ bodyLen
//        out.ensureWritable(messageLen)
//
//        out.writeShort(0xCAFE)
//        out.writeShort(messageLen)
//
//        out.writeBytes(msg,msg.readerIndex(),bodyLen)
//        val crc = CRC32.calculateBlockCRC32(out,out.readerIndex(),out.readableBytes())
//        out.writeInt(crc)
//    }
//}
