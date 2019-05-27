package com.yuyan.lightning.cache.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Method;

public class CacheInterceptor extends CacheAspectSupport implements MethodInterceptor, Serializable {

    @Override
    @Nullable
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

        CacheOperationInvoker aopAllianceInvoker = () -> {
            try {
                return invocation.proceed();
            }
            catch (Throwable ex) {
                throw new CacheOperationInvoker.ThrowableWrapper(ex);
            }
        };

        try {
            return execute(aopAllianceInvoker, invocation.getThis(), method, invocation.getArguments());
        }
        catch (CacheOperationInvoker.ThrowableWrapper th) {
            throw th.getOriginal();
        }
    }
}
