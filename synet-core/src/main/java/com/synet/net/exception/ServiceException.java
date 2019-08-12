package com.synet.net.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务业务异常
 *
 * @author konghang
 */
public class ServiceException extends RuntimeException {

    protected long code;

    public static ServiceException build(long code, String message) {
        return new ServiceException(code, message);
    }

    public static ServiceException build(long code, String message, Throwable cause) {
        return new ServiceException(code, message, cause);
    }

    public static ServiceException build(BaseExceptionConstants baseExceptionConst) {
        return new ServiceException(baseExceptionConst);
    }

    public static ServiceException build(BaseExceptionConstants baseExceptionConstant, Throwable cause) {
        return new ServiceException(baseExceptionConstant, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(long code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(long code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    public ServiceException(BaseExceptionConstants baseExceptionConstant) {
        super(baseExceptionConstant.getMessage());
        this.code = baseExceptionConstant.getCode();
    }

    public ServiceException(BaseExceptionConstants baseExceptionConstant, Throwable cause) {
        super(baseExceptionConstant.getMessage(), cause);
        this.code = baseExceptionConstant.getCode();
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code=" + code +
                ", message=" + getMessage() +
                "} " + super.toString();
    }

    public Map<String, Object> getErrorMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", this.code);
        map.put("message", this.getMessage());
        return map;
    }

}
