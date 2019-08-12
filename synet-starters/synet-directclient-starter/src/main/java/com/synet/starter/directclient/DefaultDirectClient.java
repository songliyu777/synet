package com.synet.starter.directclient;

import com.google.protobuf.Message;
import com.synet.net.http.ProtobufProtocolEncoder;
import com.synet.net.protobuf.mapping.ProtoHeader;
import com.synet.net.webclient.LightningWebClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Data
@EqualsAndHashCode
public class DefaultDirectClient implements DirectClient {

    private int id;
    private String name;
    private String ipAddr;
    private int port;
    private String url;
    private ProtobufProtocolEncoder encoder;

    public DefaultDirectClient(int id, String name, String ipAddr, int port, ProtobufProtocolEncoder encoder) {
        this.id = id;
        this.name = name;
        this.ipAddr = ipAddr;
        this.port = port;
        this.encoder = encoder;
        url = "http://" + ipAddr + ":" + port + "/pb/protocol";
    }

    @Override
    public Mono<ByteBuffer> query(ProtoHeader head, Message message) {
        LightningWebClient webClient = new LightningWebClient();
        return webClient.postProtobuf(url, encoder.encode(head, message));
    }
}
