package com.synet.server.logic.Feign;

import java.util.function.Function;

public  interface SynetLoadBalancerComandFactory extends Function<String, SynetLoadBalancerCommand<Object>> {
}
