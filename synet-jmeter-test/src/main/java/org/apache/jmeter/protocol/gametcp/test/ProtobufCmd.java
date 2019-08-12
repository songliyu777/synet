package org.apache.jmeter.protocol.gametcp.test;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ProtobufCmd {

    String name();
    /**
     * 命令
     *
     * @return
     */
    short cmd();
}
