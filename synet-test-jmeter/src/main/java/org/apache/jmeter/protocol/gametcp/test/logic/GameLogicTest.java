package org.apache.jmeter.protocol.gametcp.test.logic;

import com.google.protobuf.Message;
import com.synet.net.protobuf.mapping.*;
import com.synet.protobuf.Syprotocol;
import org.apache.jmeter.protocol.gametcp.test.ProcessDefine;
import org.apache.jmeter.protocol.gametcp.test.ProtobufCmd;

@ProtobufController
public class GameLogicTest {

    @ProtobufMapping(cmd = (short) Syprotocol.protocol_id.connect_msg_VALUE)
    public String connect(@Header ProtoHeader head, @Body Syprotocol.stc_connect stc) {
        return ProcessDefine.NEXT;
    }

    @ProtobufMapping(cmd = (short) Syprotocol.protocol_id.login_msg_VALUE)
    public String login(@Header ProtoHeader head, @Body Syprotocol.stc_Login stc) {
        return ProcessDefine.NEXT;
    }

    @ProtobufCmd(name = "login", cmd = (short) Syprotocol.protocol_id.login_msg_VALUE)
    public Message login(String account, String password) {
        Syprotocol.cts_Login cts = Syprotocol.cts_Login.newBuilder().setAccount(account).setPassword(password).build();
        return cts;
    }
}
