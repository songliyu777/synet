package com.synet.starter.feign;

import java.util.function.Function;

public interface SynetLoadBalancerCommandFactory extends Function<String, SynetLoadBalancerCommand<Object>> {
}
