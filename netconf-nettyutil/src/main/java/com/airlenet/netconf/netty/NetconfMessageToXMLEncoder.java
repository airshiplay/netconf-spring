package com.airlenet.netconf.netty;

import com.tailf.jnc.Element;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author airlenet
 * @version 2017-09-20
 */
public class NetconfMessageToXMLEncoder extends MessageToByteEncoder<NetconfMessage> {
    private final Optional<String> clientId;

    public NetconfMessageToXMLEncoder(){
        this(Optional.empty());
    }
    public NetconfMessageToXMLEncoder(final Optional<String> clientId) {
        this.clientId = clientId;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NetconfMessage message, ByteBuf byteBuf) throws Exception {

        if(clientId.isPresent()){

        }
        try(ByteBufOutputStream outputStream = new ByteBufOutputStream(byteBuf)){
            StreamResult result = new StreamResult(new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)));
            DOMSource source = new DOMSource(message.getDocument());

        }

    }
}
