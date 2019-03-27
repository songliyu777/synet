package com.synet.net.protobuf.mapping;

import com.google.common.collect.Maps;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Map;

public class ProtobufMappingHandlerMapping extends ApplicationObjectSupport implements InitializingBean {

    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    private Map<Short, ProtobufMethod> registerMap = Maps.newHashMap();

    /**
     * Scan beans in the ApplicationContext, detect and register handler methods.
     * @see #isHandler
     * @see #detectHandlerMethods
     * @see #handlerMethodsInitialized
     */
    protected void initProtobufMethods() {
        String[] beanNames = obtainApplicationContext().getBeanNamesForType(Object.class);

        for (String beanName : beanNames) {
            if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                Class<?> beanType = null;
                try {
                    beanType = obtainApplicationContext().getType(beanName);
                }
                catch (Throwable ex) {
                    // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
                    }
                }
                if (beanType != null && isProtobuf(beanType)) {
                    detectHandlerMethods(beanName);
                }
            }
        }
//        handlerMethodsInitialized(getHandlerMethods());
    }

    private boolean isProtobuf(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, ProtobufController.class);
    }

    /**
     * Look for handler methods in the specified handler bean.
     * @param handler either a bean name or an actual handler instance
     * @see #getMappingForMethod
     */
    protected void detectHandlerMethods(Object handler) {
        Class<?> handlerType = (handler instanceof String ?
                obtainApplicationContext().getType((String) handler) : handler.getClass());

        if (handlerType != null) {
            Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, ProtobufMappingInfo> methods = MethodIntrospector.selectMethods(userType,
                    (MethodIntrospector.MetadataLookup<ProtobufMappingInfo>) method -> {
                        try {
                            return getMappingForMethod(method, userType);
                        }
                        catch (Throwable ex) {
                            throw new IllegalStateException("Invalid mapping on handler class [" +
                                    userType.getName() + "]: " + method, ex);
                        }
                    });
            if (logger.isDebugEnabled()) {
                logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
            }
            methods.forEach((method, mapping) -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                registerProtobufMethod(handler, invocableMethod, mapping);
            });
        }
    }

    private ProtobufMappingInfo getMappingForMethod(Method method, Class<?> userType) {
        ProtobufMapping mapping = AnnotatedElementUtils.findMergedAnnotation(method, ProtobufMapping.class);
        return new ProtobufMappingInfo(mapping.cmd());
    }

    private void registerProtobufMethod(Object handler, Method invocableMethod, ProtobufMappingInfo mapping) {
        ProtobufMethod protobufMethod = createHandlerMethod(handler, invocableMethod);
        registerMap.put(mapping.getCmd(), protobufMethod);
    }

    protected ProtobufMethod createHandlerMethod(Object handler, Method method) {
        ProtobufMethod handlerMethod;
        if (handler instanceof String) {
            String beanName = (String)handler;
            handlerMethod = new ProtobufMethod(beanName, this.obtainApplicationContext().getAutowireCapableBeanFactory(), method);
        } else {
            handlerMethod = new ProtobufMethod(handler, method);
        }

        return handlerMethod;
    }

    public ProtobufMethod getProtobufMethod(short cmd) {
        return registerMap.get(cmd);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.initProtobufMethods();
    }
}
