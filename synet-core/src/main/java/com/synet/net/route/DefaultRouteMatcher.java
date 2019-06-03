package com.synet.net.route;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 路由默认匹配器
 *
 * @author konghang
 */
public class DefaultRouteMatcher implements RouteMatcher{

    private volatile List<RouteDefinition> routeDefinitions;
    private volatile List<Route> routes;

    private Map<Short, Route> routeMap = Maps.newConcurrentMap();

    /**
     * 路由默认匹配器
     *
     * @param routeDefinitions 路由
     */
    public DefaultRouteMatcher(List<RouteDefinition> routeDefinitions) {
        if (Objects.isNull(routeDefinitions)) {
            routeDefinitions = Lists.newArrayList();
        }
        this.routeDefinitions = routeDefinitions;
        this.routes = routeDefinitions.stream().map(routeDefinition -> new Route(routeDefinition.getId(), routeDefinition.getUri(), routeDefinition.getOrder(), routeDefinition.getPredicates())).collect(Collectors.toList());;
    }

    @Override
    public Route match(short cmd) {
        Route route = routeMap.get(cmd);
        if (Objects.nonNull(route)) {
            return route;
        }

        route = findRouteFromList(cmd);
        if (Objects.nonNull(route)) {
            routeMap.put(cmd, route);
        }
        return route;
    }

    private Route findRouteFromList(short cmd) {
        for (Route route : this.routes) {
            for (PredicateDefinition predicate : route.getPredicates()) {
                if (cmd >= predicate.getBeginCmd() && cmd <= predicate.getEndCmd()) {
                    return route;
                }
            }
        }
        return null;
    }

    @Override
    public List<Route> getRoutes() {
        return this.routes;
    }
}
