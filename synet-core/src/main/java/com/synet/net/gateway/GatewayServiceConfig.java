package com.synet.net.gateway;

import com.synet.net.route.RouteDefinition;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *       - id: login-service
 *         uri: lb://login-service
 *         order: 9000
 *         predicates:
 *         - Cmd=1
 *         - Cmd=2
 *         - Cmd=100~300
 *
 */
@Data
public class GatewayServiceConfig {
    private List<RouteDefinition> routes = new ArrayList<>();
}