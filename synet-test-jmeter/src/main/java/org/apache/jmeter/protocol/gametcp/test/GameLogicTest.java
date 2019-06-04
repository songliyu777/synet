package org.apache.jmeter.protocol.gametcp.test;

import com.google.protobuf.Message;
import com.synet.net.protobuf.mapping.*;
import com.synet.protobuf.Syprotocol;

@ProtobufController
public class GameLogicTest {

    @ProtobufMapping(cmd = (short) Syprotocol.protocol_id.login_msg_VALUE)
    public String login(@Header ProtoHeader head, @Body Syprotocol.stc_Login stc) {
        return "login";
    }

    @ProtobufCmd(name = "login", cmd = (short) Syprotocol.protocol_id.login_msg_VALUE)
    public Message login(String account, String password) {
        Syprotocol.cts_Login cts = Syprotocol.cts_Login.newBuilder().setAccount(account).setPassword(password).build();
        return cts;
    }
}
