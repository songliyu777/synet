package com.synet.starter.directclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synet.net.http.ProtobufProtocolEncoder;
import com.synet.net.webclient.LightningWebClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DirectClientManager implements ApplicationListener {

    private List<String> directClients;

    @Value("${eureka.client.serviceUrl.defaultZone}")
    private String eurekaUrl;

    @Autowired
    ProtobufProtocolEncoder encoder;

    protected ConcurrentHashMap<Integer, DirectClient> idClients = new ConcurrentHashMap<>();

    protected ConcurrentHashMap<String, List<DirectClient>> nameClients = new ConcurrentHashMap<>();

    public DirectClientManager(List<String> directClients) {
        this.directClients = directClients;
    }

    public DirectClient getDirectClient(int id_hashcode) {
        return idClients.get(id_hashcode);
    }

    public List<DirectClient> getDirectClients(String name) {
        return nameClients.get(name);
    }

    private void updateClients() {
        for (String appname : directClients) {
            LightningWebClient webClient = new LightningWebClient();
            webClient.getJson(eurekaUrl + "apps/" + appname, String.class)
                    .doOnError(e -> log.error(e.toString()))
                    .subscribe(result -> paserJson(appname, result));
        }
    }

    void paserJson(String name, String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode app = root.get("application");
            if (app != null) {
                JsonNode instances = app.get("instance");
                int size = instances.size();
                List<DirectClient> lstClient = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    JsonNode instance = instances.get(i);
                    String instanceId = instance.get("instanceId").asText();
                    String ipAddr = instance.get("ipAddr").asText();
                    int port = instance.get("port").get("$").asInt();
                    DefaultDirectClient client = new DefaultDirectClient(instanceId.hashCode(), name, ipAddr, port, encoder);
                    updateClient(client);
                    lstClient.add(client);
                }
                updateClient(name, lstClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void updateClient(DirectClient client) {
        DirectClient c = idClients.get(client.getId());
        if (c == null) {
            idClients.put(client.getId(), client);
        } else if (!c.equals(client)) {
            idClients.replace(client.getId(), client);
        }
    }

    void updateClient(String name, List<DirectClient> lstClient) {
        List<DirectClient> list = nameClients.get(name);
        if (list == null) {
            nameClients.put(name, lstClient);
        } else if (list.size() != lstClient.size()) {
            for (DirectClient c : list) {
                if (lstClient.indexOf(c) == -1) {
                    idClients.remove(c.getId());
                }
            }
            nameClients.replace(name, lstClient);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationReadyEvent ||
                applicationEvent instanceof HeartbeatEvent) {
            updateClients();
        }
    }
}
