package com.synet.starter.gatewayservice.service;

import com.google.common.collect.Maps;
import com.synet.net.route.Route;
import org.apache.commons.lang3.StringUtils;
import org.springside.modules.utils.collection.CollectionUtil;
import reactivefeign.cloud.CloudReactiveFeign;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LightningServiceLocator {

    private final static String SERVICE_PREFIX = "lb://";

    //key-routerId : service
    private Map<String, LightningPbService> routeServiceMap = Maps.newHashMap();

    //key-servicename : service
    private Map<String, LightningPbService> serviceServiceMap = Maps.newHashMap();

    private CloudReactiveFeign.Builder<LightningPbService> builder;

    public LightningServiceLocator(List<Route> routes, CloudReactiveFeign.Builder builder) {
        this.builder = builder;
        initService(routes);
    }

    private void initService(List<Route> routes) {
        if (CollectionUtil.isEmpty(routes)) {
            return;
        }

        routes.forEach(route -> {
            String uri = route.getUri();
            String serviceName = StringUtils.remove(uri, SERVICE_PREFIX);
            LightningPbService lightningPbService = serviceServiceMap.get(serviceName);
            if (Objects.isNull(lightningPbService)) {
                lightningPbService = createService(serviceName);
                serviceServiceMap.put(serviceName, lightningPbService);
            }
            routeServiceMap.put(route.getId(), lightningPbService);
        });
    }

    private LightningPbService createService(String service) {
        return builder.target(LightningPbService.class, "http://" + service);
    }

    public LightningPbService match(Route route) {
        return match(route.getId());
    }

    public LightningPbService match(String routeId) {
        return routeServiceMap.get(routeId);
    }
}
