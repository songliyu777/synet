package com.synet.server.logic.Feign;

import java.util.function.Function;

public interface SynetLoadBalancerCommandFactory extends Function<String, SynetLoadBalancerCommand<Object>> {
}
