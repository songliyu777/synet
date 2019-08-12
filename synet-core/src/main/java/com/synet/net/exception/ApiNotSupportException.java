package com.synet.net.exception;

import java.util.Objects;

/**
 * api未支持异常
 *
 * @author konghang
 */
public class ApiNotSupportException extends RuntimeException{

    private volatile static ApiNotSupportException INSTANCE;

    public ApiNotSupportException() {
        super("not supported by provider");
    }

    public static ApiNotSupportException getInstance(){
        if (Objects.isNull(INSTANCE)){
            INSTANCE = new ApiNotSupportException();
        }
        return INSTANCE;
    }
}
