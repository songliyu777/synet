package com.synet.starter.directclient;

import com.google.protobuf.Message;
import com.synet.net.protobuf.mapping.ProtoHeader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.List;

public class DefaultDirectRemoteClient implements SynetDirectRemoteClient {

    DirectClientManager manager;

    public DefaultDirectRemoteClient(DirectClientManager manager) {
        this.manager = manager;
    }

    @Override
    public Mono<ByteBuffer> query(int id_hashcode, ProtoHeader head, Message message) {
        DirectClient client = manager.getDirectClient(id_hashcode);
        if (client == null) {
            return Mono.empty();
        }
        return client.query(head, message);
    }

    @Override
    public Flux<ByteBuffer> query(String name, ProtoHeader head, Message message) {
        List<DirectClient> clients = manager.getDirectClients(name);
        if (clients == null) {
            return Flux.empty();
        }
        return Flux.fromIterable(clients).flatMap(c -> c.query(head, message));
    }
}
