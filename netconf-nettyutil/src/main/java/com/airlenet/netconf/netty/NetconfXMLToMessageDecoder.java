package com.airlenet.netconf.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author airlenet
 * @version 2017-09-20
 */
public class NetconfXMLToMessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.isReadable()){
            ByteBufInputStream inputStream = new ByteBufInputStream(byteBuf);
            inputStream.readUTF();
        }

    }
}
