package com.synet.server.gateway.feign;

import com.synet.server.gateway.configuration.ProtoFeignConfiguration;
import com.synet.server.gateway.protobuf.TestOuterClass;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "server-logic", configuration = ProtoFeignConfiguration.class)
public interface MessageClient {
    @RequestMapping(value = "/test", method = RequestMethod.GET, consumes = "application/x-protobuf", produces = "application/x-protobuf")
    TestOuterClass.Test test();
}
