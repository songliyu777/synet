package com.synet.cache.interceptor;

import com.synet.cache.ReactiveCache;
import com.synet.cache.expression.CachedExpressionEvaluator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class CacheOperationExpressionEvaluator extends CachedExpressionEvaluator {

    /**
     * Indicate that there is no result variable.
     */
    public static final Object NO_RESULT = new Object();

    /**
     * Indicate that the result variable cannot be used at all.
     */
    public static final Object RESULT_UNAVAILABLE = new Object();

    /**
     * The name of the variable holding the result object.
     */
    public static final String RESULT_VARIABLE = "result";


    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> keyCache = new ConcurrentHashMap<>(64);

    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> conditionCache = new ConcurrentHashMap<>(64);

    private final Map<CachedExpressionEvaluator.ExpressionKey, Expression> unlessCache = new ConcurrentHashMap<>(64);


    /**
     * Create an {@link EvaluationContext}.
     * @param caches the current caches
     * @param method the method
     * @param args the method arguments
     * @param target the target object
     * @param targetClass the target class
     * @param result the return value (can be {@code null}) or
     * {@link #NO_RESULT} if there is no return at this time
     * @return the evaluation context
     */
    public EvaluationContext createEvaluationContext(Collection<? extends ReactiveCache> caches,
                                                     Method method, Object[] args, Object target, Class<?> targetClass, Method targetMethod,
                                                     @Nullable Object result, @Nullable BeanFactory beanFactory) {

        CacheExpressionRootObject rootObject = new CacheExpressionRootObject(
                caches, method, args, target, targetClass);
        CacheEvaluationContext evaluationContext = new CacheEvaluationContext(
                rootObject, targetMethod, args, getParameterNameDiscoverer());
        if (result == RESULT_UNAVAILABLE) {
            evaluationContext.addUnavailableVariable(RESULT_VARIABLE);
        }
        else if (result != NO_RESULT) {
            evaluationContext.setVariable(RESULT_VARIABLE, result);
        }
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return evaluationContext;
    }

    @Nullable
    public Object key(String keyExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return getExpression(this.keyCache, methodKey, keyExpression).getValue(evalContext);
    }

    public boolean condition(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return (Boolean.TRUE.equals(getExpression(this.conditionCache, methodKey, conditionExpression).getValue(
                evalContext, Boolean.class)));
    }

    public boolean unless(String unlessExpression, AnnotatedElementKey methodKey, EvaluationContext evalContext) {
        return (Boolean.TRUE.equals(getExpression(this.unlessCache, methodKey, unlessExpression).getValue(
                evalContext, Boolean.class)));
    }

    /**
     * Clear all caches.
     */
    void clear() {
        this.keyCache.clear();
        this.conditionCache.clear();
        this.unlessCache.clear();
    }
}
