package com.synet.net.protobuf.mapping;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Protobuf协议控制器注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ProtobufController {
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";
}
