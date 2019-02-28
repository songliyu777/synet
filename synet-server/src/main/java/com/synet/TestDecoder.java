package com.synet;

import com.synet.protocol.TcpNetProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class TestDecoder extends ByteToMessageDecoder {
    public static String GetThreadId() {
        return " [tid:" + Thread.currentThread().getId() + "]";
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("orginal:" + in.readableBytes() + GetThreadId());
        TcpNetProtocol protocol = TcpNetProtocol.Parse(in);
        out.add(protocol);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        //cause.printStackTrace();
        //ctx.channel().close();
    }
}
