package com.synet.net.protobuf.mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProtobufMapping {

    /**
     * 命令
     *
     * @return
     */
    short cmd();

}
