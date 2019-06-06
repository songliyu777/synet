package org.apache.jmeter.protocol.gametcp.test;

import com.synet.net.protocol.NetProtocol;
import com.synet.net.protocol.ProtocolHeadDefine;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Main {
    public static void main(String[] args) {
        Syprotocol.stc_Login stc = Syprotocol.stc_Login.newBuilder().setState(0).build();
        Syprotocol.cts_Login cts = Syprotocol.cts_Login.newBuilder().setAccount("samuel1").setPassword("123456").build();
        NetProtocol protocol = NetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD, ProtocolHeadDefine.VERSION, 0xfffe, (short) Syprotocol.protocol_id.login_msg_VALUE, 0, stc.toByteArray());
        MessageHandler handler = new MessageHandler();
        handler.handle(ByteBuffer.wrap(protocol.toArray()));
        try {
            handler.send(null,"login samuel1 123456");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
