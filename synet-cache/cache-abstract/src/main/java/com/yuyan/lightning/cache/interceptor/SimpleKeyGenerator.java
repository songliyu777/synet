package com.yuyan.lightning.cache.interceptor;

import org.springframework.cache.interceptor.SimpleKey;

import java.lang.reflect.Method;

public class SimpleKeyGenerator implements KeyGenerator{

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return generateKey(params);
    }

    /**
     * Generate a key based on the specified parameters.
     */
    public static Object generateKey(Object... params) {
        if (params.length == 0) {
            return SimpleKey.EMPTY;
        }
        if (params.length == 1) {
            Object param = params[0];
            if (param != null && !param.getClass().isArray()) {
                return param;
            }
        }
        return new SimpleKey(params);
    }
}
