package com.synet.net.route;

import java.util.List;

/**
 * 路由匹配
 *
 * @author konghang
 */
public interface RouteMatcher {

    /**
     * 匹配
     *
     * @param cmd 命令号
     * @return 路由
     */
    Route match(short cmd);

    /**
     * 获取路由
     *
     * @return
     */
    List<Route> getRoutes();
}
