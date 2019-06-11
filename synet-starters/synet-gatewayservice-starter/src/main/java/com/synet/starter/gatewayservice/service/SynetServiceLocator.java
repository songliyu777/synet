package com.synet.starter.gatewayservice.service;

import com.google.common.collect.Maps;
import com.synet.net.route.Route;
import org.apache.commons.lang3.StringUtils;
import org.springside.modules.utils.collection.CollectionUtil;
import reactivefeign.cloud.CloudReactiveFeign;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SynetServiceLocator {

    private final static String SERVICE_PREFIX = "lb://";

    //key-routerId : service
    private Map<String, SynetPbService> routeServiceMap = Maps.newHashMap();

    //key-servicename : service
    private Map<String, SynetPbService> serviceServiceMap = Maps.newHashMap();

    private CloudReactiveFeign.Builder<SynetPbService> builder;

    public SynetServiceLocator(List<Route> routes, CloudReactiveFeign.Builder builder) {
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
            SynetPbService synetPbService = serviceServiceMap.get(serviceName);
            if (Objects.isNull(synetPbService)) {
                synetPbService = createService(serviceName);
                serviceServiceMap.put(serviceName, synetPbService);
            }
            routeServiceMap.put(route.getId(), synetPbService);
        });
    }

    private SynetPbService createService(String service) {
        return builder.target(SynetPbService.class, "http://" + service);
    }

    public SynetPbService match(Route route) {
        return match(route.getId());
    }

    public SynetPbService match(String routeId) {
        return routeServiceMap.get(routeId);
    }
}
